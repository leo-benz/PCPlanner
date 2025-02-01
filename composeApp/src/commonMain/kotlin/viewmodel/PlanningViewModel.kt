@file:OptIn(ExperimentalUuidApi::class)

package viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import model.*
import org.docx4j.XmlUtils
import org.docx4j.model.datastorage.migration.VariablePrepare
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import repository.JubilareRepository
import repository.StandchenRepository
import java.io.File
import java.time.DayOfWeek
import kotlin.math.log
import kotlin.uuid.ExperimentalUuidApi

class PlanningViewModel(
    private val standchenRepository: StandchenRepository,
    private val jubilareRepository: JubilareRepository
) : ViewModel() {

    var dateDetails: MutableState<LocalDate?> = mutableStateOf(null)

    var year = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year)

//    var isInitialized: StateFlow<Boolean> = year.flatMapLatest { year ->
//        standchenRepository.getStandchen(year).map { it.isNotEmpty() }
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val errorMessage = MutableStateFlow<String?>(null)

    var holiday: StateFlow<Holiday?> = year.flatMapLatest { year ->
        standchenRepository.getSummerHoliday(year).onStart { println("Flow started for year = $year") }
            .onEach { holiday ->
                println("Flow emitted holiday = $holiday")
                if (holiday != null) {
                    val standchen = standchenRepository.getStandchen(year).first()
                    if (standchen.isEmpty()) {
                        generateInitialStandchen(holiday)
                    }
                    assignJubilareToStandchen()
                }
            }
            .onCompletion { cause -> println("Flow completed. Cause: $cause") }
    }.catch { e ->
        println("Flow caught error: $e")
        e.printStackTrace()
//        errorMessage.value = "Error fetching summer holiday, please configure holidays manually!\n${e.message}"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun updateYear(newYear: Int) {
        year.value = newYear
    }

    suspend fun generateInitialStandchen(holiday: Holiday) {
        // Create a standchen for every second sunday starting from the first sunday in the year
        // Skip the second sunday in a month

        val standchenList = mutableListOf<Standchen>()
        val firstSunday = getFirstSunday(year.value, 1)
        var day = firstSunday
        var month = firstSunday.month
        var sundayCount = 1
        var skipWeek = false
        while (day.year == year.value) {
            if (day in holiday.startDate..holiday.endDate) {
                skipWeek = true
            }
            if (sundayCount == 2) {
                skipWeek = true
            }
            if (!skipWeek) {
                standchenList.add(Standchen(day))
            }
            day = day.plusDays(7)
            skipWeek = !skipWeek
            sundayCount++
            if (day.month != month) {
                month = day.month
                sundayCount = 1
            }
        }
        standchenRepository.insert(standchenList)
    }


    private fun filterEligibleJubilare(jubilare: List<Jubilar>): List<Jubilar> {
        return jubilare.filter {
            val age = year.value - it.originalJubilarDate.year
            var eligible = age == 80 || age >= 85
            if (it is AnniversaryJubilar) {
                eligible = eligible || it.marriageAnniversary(year.value) != MarriageAnniversary.NONE
            }

            return@filter eligible
        }
    }

    suspend fun assignJubilareToStandchen() {
        val standchenList = standchenRepository.getStandchen(year.value).first()
        var jubilare = jubilareRepository.getJubilare().first()
        jubilare = filterEligibleJubilare(jubilare)

        jubilare = jubilare.sortedBy {
            LocalDate(
                year.value,
                it.originalJubilarDate.monthNumber,
                it.originalJubilarDate.dayOfMonth
            )
        }
        var currentStandchen = standchenList.first()
        for (ju in jubilare) {
            val currentBirthday =
                LocalDate(year.value, ju.originalJubilarDate.monthNumber, ju.originalJubilarDate.dayOfMonth)
            if (currentStandchen.date < currentBirthday) {
                currentStandchen = standchenList.first { it.date > currentBirthday }
            }
            invite(ju, currentStandchen)
        }
    }


    private fun invite(jubilar: Jubilar, standchen: Standchen) {
        val invite = StandchenInvite(0, false, standchen.date, jubilar.jubilarId!!)
        standchenRepository.insert(invite)
    }

    private fun getFirstSunday(year: Int, month: Int): LocalDate {
        var day = LocalDate(year, month, 1)
        while (day.dayOfWeek != DayOfWeek.SUNDAY) {
            day = day.plusDays(1)
        }
        return day
    }

    fun isHoliday(day: LocalDate): Flow<Boolean> {
        return holiday.map { holiday ->
            holiday != null && day in holiday.startDate..holiday.endDate
        }
    }

    fun isStandchen(day: LocalDate): Flow<Boolean> {
        return standchenRepository.getStandchen(year.value).map { standchenList ->
            standchenList.any { it.date == day }
        }
    }

    fun isJubilarDay(day: LocalDate): Flow<Boolean> {
        return jubilareRepository.getJubilare().map { jubilareList ->
            val eligibleJubilare = filterEligibleJubilare(jubilareList)
            eligibleJubilare.any { it.originalJubilarDate.monthNumber == day.monthNumber && it.originalJubilarDate.dayOfMonth == day.dayOfMonth }
        }
    }

    fun onDaySelected(day: LocalDate) {
        viewModelScope.launch {
            println("Day selected: $day")
            println("Standchen: ${standchenRepository.getStandchenWithJubilare(day).first()}")
//            println("Jubilare: ${jubilareRepository.getJubilareWithInvites(day).first()}")

            dateDetails.value = day;
        }
    }

    fun dismissDateDetails() {
        dateDetails.value = null
    }

    fun toggleStandchen(date: LocalDate) {
        viewModelScope.launch {
            val standchen = standchenRepository.getStandchen(date).first()
            if (standchen != null) {
                standchenRepository.remove(standchen)
            } else {
                standchenRepository.insert(Standchen(date))
            }
            assignJubilareToStandchen()
        }
    }

    fun jubilare(date: LocalDate): Flow<List<Jubilar>> {
        val jubilareFlow = jubilareRepository.getJubilare(date).map { filterEligibleJubilare(it) ?: emptyList() }
        val standchenJubilareFlow =
            standchenRepository.getStandchenWithJubilare(date).map { it?.jubilare ?: emptyList() }
        return combine(jubilareFlow, standchenJubilareFlow) { jubilare, standchenJubilare ->
            (jubilare + standchenJubilare).distinctBy { it.jubilarId }
        }
    }

    // TODO: Use different template for standchen after sommerferien

    fun print(jubilare: List<Jubilar>, year: Int, file: File) {
        viewModelScope.launch {
            val logFile = File(file.absolutePath + ".txt")
            logFile.appendText("Printing letter for ${jubilare.size} jubilare on ${getCurrentLocalDate()}\n")
            val regularDocxResource = this::class.java.getResourceAsStream("/Anschreiben_Jubilare.docx")
                ?: error("Resource not found!")
            val holidaysDocxResource = this::class.java.getResourceAsStream("/Anschreiben_Jubilare_Ferien.docx")
                ?: error("Resource not found!")

            val regularWordMLPackage = WordprocessingMLPackage.load(regularDocxResource)
            val regularMainDocumentPart: MainDocumentPart = regularWordMLPackage.mainDocumentPart
            VariablePrepare.prepare(regularWordMLPackage)
            val regularTemplate = XmlUtils.deepCopy(regularMainDocumentPart.contents)

            logFile.appendText("Loaded regular templates\n")

            val holidaysWordMLPackage = WordprocessingMLPackage.load(holidaysDocxResource)
            val holidaysMainDocumentPart: MainDocumentPart = holidaysWordMLPackage.mainDocumentPart
            VariablePrepare.prepare(holidaysWordMLPackage)
            val holidaysTemplate = XmlUtils.deepCopy(holidaysMainDocumentPart.contents)

            logFile.appendText("Loaded holiday templates\n")

            regularMainDocumentPart.contents.body.content.clear()

            logFile.appendText("Cleared regular template\n")

            for (jubilar in jubilare) {
                logFile.appendText("Printing letter for ${jubilar.lastName}\n")
                val standchen = standchenRepository.getStandchen(jubilar, year).first()
                val letterDate = getCurrentLocalDate()
                val placeholders = mapOf(
                    "firstName" to if (jubilar is BirthdayJubilar) jubilar.firstName else "Ehepaar",
                    "lastName" to jubilar.lastName,
                    "address" to jubilar.address,
                    "letterDate" to letterDate.format(LocalDate.Format {
                        monthName(MonthNames.GERMAN_FULL); char(
                        ' '
                    ); year()
                    }),
                    "greeting" to "Sehr geehrte${
                        if (jubilar is BirthdayJubilar) {
                            when (jubilar.gender) {
                                Gender.MALE -> "r Herr"; Gender.FEMALE -> " Frau"; else -> "s "
                            }
                        } else {
                            "s Ehepaar"
                        }
                    }",
                    "eventPronoun" to if (jubilar is AnniversaryJubilar) "Ihrer" else "Ihrem",
                    "eventDescription" to if (jubilar is AnniversaryJubilar) {
                        when (jubilar.marriageAnniversary()) {
                            MarriageAnniversary.DIAMOND -> "Diamantene Hochzeit"
                            MarriageAnniversary.IRON -> "Eiserne Hochzeit"
                            MarriageAnniversary.GOLDEN -> "Goldene Hochzeit"
                            MarriageAnniversary.PLATINUM -> "Gnaden Hochzeit"
                            else -> "Hochzeit"
                        }
                    } else {
                        "${year - jubilar.originalJubilarDate.year}. Geburtstag"
                    },
                    "standchenDate" to standchen.date.format(LocalDate.Format {
                        dayOfMonth(); char('.'); monthNumber(); char(
                        '.'
                    ); year()
                    }),
                    "standchenFeedbackDate" to standchen.date.minusDays(5)
                        .format(LocalDate.Format { dayOfMonth(); char('.'); monthNumber(); char('.'); year() })
                );
                logFile.appendText("Created placeholders\n")
                logFile.appendText("Checking if standchen is after holiday ${holiday.value}\n")
                val isHolidayStandchen = holiday.value?.let {
                    standchenRepository.isFirstAfterHoliday(standchen, it, logFile)
                } ?: false
                logFile.appendText("Is holiday standchen: $isHolidayStandchen\n")
                regularMainDocumentPart.contents.body.content.addAll(if (isHolidayStandchen) holidaysTemplate.body.content else regularTemplate.body.content)
                logFile.appendText("Added template\n")
                regularMainDocumentPart.variableReplace(placeholders)
                logFile.appendText("Replaced placeholders\n")
            }

            // Save the new document
            regularWordMLPackage.save(file)
            logFile.appendText("Saved document with file: ${file.absolutePath}\n")
            println("printed!!!")
        }
    }

    fun configureSummerHolidays(start: LocalDate, end: LocalDate) {
        viewModelScope.launch {
            standchenRepository.insert(Holiday(start, end))
        }
    }

    // TODO: Add print feature for whole month

    fun exportStandchenCsv(year: Int, file: File) {
        viewModelScope.launch {
            try {
                val standchenList = standchenRepository.getStandchenWithJubilare(year).first() // Fetch Standchen for the year
                val csvContent = buildCsvContent(standchenList)
                saveCsvToFile(file, csvContent)
                // Notify the user (e.g., with a Snackbar) that the export was successful
            } catch (e: Exception) {
                // Handle errors (e.g., notify the user of failure)
            }
        }
    }

    private fun buildCsvContent(standchenList: List<StandchenWithJubilare>): String {
        val header = "Name,Datum,Anlass,Adresse,Antwort\n"
        val rows = standchenList.joinToString("") { standchen ->
            if (!standchen.jubilare.isEmpty()) {
                standchen.jubilare.joinToString("\n") { jubilar ->
                    val name =
                        if (jubilar is BirthdayJubilar) "${jubilar.firstName} ${jubilar.lastName}" else "Ehepaar" + jubilar.lastName
                    val anlass =
                        if (jubilar is AnniversaryJubilar) when (jubilar.marriageAnniversary()) {
                            MarriageAnniversary.DIAMOND -> "Diamantene Hochzeit"
                            MarriageAnniversary.IRON -> "Eiserne Hochzeit"
                            MarriageAnniversary.GOLDEN -> "Goldene Hochzeit"
                            MarriageAnniversary.PLATINUM -> "Gnaden Hochzeit"
                            else -> "Hochzeit"
                        } else "${year.value - jubilar.originalJubilarDate.year}. Geburtstag"
                    "${name},${standchen.standchen.date.format(LocalDate.Format { dayOfMonth(); chars(". "); monthName(MonthNames.GERMAN_FULL) })},${anlass},${jubilar.address},\n"
                }
            } else {
                ""
            }
        }
        return header + rows
    }

    private fun saveCsvToFile(file: File, content: String) {

        file.writeText(content)
    }
}

private fun LocalDate.plusDays(i: Int): LocalDate {
    return this.plus(i, DateTimeUnit.DAY)
}

private fun LocalDate.minusDays(i: Int): LocalDate {
    return this.minus(i, DateTimeUnit.DAY)
}

fun getCurrentLocalDate(): LocalDate {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}
