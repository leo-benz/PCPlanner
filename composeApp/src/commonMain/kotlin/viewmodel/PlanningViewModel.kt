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
import repository.SummerHolidayFetchException
import java.io.File
import java.time.DayOfWeek
import kotlin.uuid.ExperimentalUuidApi

class PlanningViewModel(
    private val standchenRepository: StandchenRepository,
    private val jubilareRepository: JubilareRepository
) : ViewModel() {

    var dateDetails: MutableState<LocalDate?> = mutableStateOf(null)

    var year = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year)

    var isInitialized: StateFlow<Boolean> = year.flatMapLatest { year ->
        standchenRepository.getStandchen(year).map { it.isNotEmpty() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val errorMessage = MutableStateFlow<String?>(null)

    var holiday: StateFlow<Holiday?> = year.flatMapLatest { year ->
        standchenRepository.getSummerHoliday(year).onStart { println("Flow started for year = $year") }
            .onEach { holiday -> println("Flow emitted holiday = $holiday") }
            .onCompletion { cause -> println("Flow completed. Cause: $cause") }
    }.catch { e ->
        println("Flow caught error: $e")
        errorMessage.value = "Error fetching summer holiday, please configure holidays manually!\n${e.message}"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun updateYear(newYear: Int) {
        year.value = newYear
    }

    fun generateInitialStandchen() {
        // Create a standchen for every second sunday starting from the first sunday in the year
        // Skip the second sunday in a month
        viewModelScope.launch {
            holiday.first().let { holiday ->
                val standchenList = mutableListOf<Standchen>()
                val firstSunday = getFirstSunday(year.value, 1)
                var day = firstSunday
                var month = firstSunday.month
                var sundayCount = 1
                var skipWeek = false
                while (day.year == year.value) {
                    if (holiday != null && day in holiday.startDate..holiday.endDate) {
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
        }
    }

    fun assignJubilareToStandchen() {
        viewModelScope.launch {
            val standchenList = standchenRepository.getStandchen(year.value).first()
            var jubilare = jubilareRepository.getJubilare().first()
            jubilare = jubilare.filter {
                val age = year.value - it.originalJubilarDate.year
                var eligible = age == 80 || age >= 85
                if (it is AnniversaryJubilar) {
                    eligible = eligible || it.marriageAnniversary(year.value) != MarriageAnniversary.NONE
                }

                eligible = true // FIXME: Remove this line to enable the age check

                return@filter eligible
            }
            jubilare = jubilare.sortedBy { LocalDate(year.value, it.originalJubilarDate.monthNumber, it.originalJubilarDate.dayOfMonth) }
            var currentStandchen = standchenList.first()
            for (ju in jubilare) {
                val currentBirthday = LocalDate(year.value, ju.originalJubilarDate.monthNumber, ju.originalJubilarDate.dayOfMonth)
                if (currentStandchen.date < currentBirthday) {
                    currentStandchen = standchenList.first { it.date > currentBirthday }
                }
                invite(ju, currentStandchen)
            }
        }
    }

    private fun invite(jubilar: Jubilar, standchen: Standchen) {
        var invite = StandchenInvite(0, false, standchen.date, jubilar.jubilarId)
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
            jubilareList.any { it.originalJubilarDate.monthNumber == day.monthNumber && it.originalJubilarDate.dayOfMonth == day.dayOfMonth }
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
        val jubilareFlow = jubilareRepository.getJubilare(date)
        val standchenJubilareFlow = standchenRepository.getStandchenWithJubilare(date).map { it?.jubilare ?: emptyList() }
        return combine(jubilareFlow, standchenJubilareFlow) { jubilare, standchenJubilare ->
            (jubilare + standchenJubilare).distinctBy { it.jubilarId }
        }
    }

    // TODO: Use different template for standchen after sommerferien

    fun print(jubilare: List<Jubilar>, year: Int) {
        viewModelScope.launch {

            val wordMLPackage = WordprocessingMLPackage.load(File("/Users/leobenz/Developer/Private/PCPlanner/composeApp/template/Anschreiben_Jubilare.docx"))
            val mainDocumentPart: MainDocumentPart = wordMLPackage.mainDocumentPart
            VariablePrepare.prepare(wordMLPackage)
            val template = XmlUtils.deepCopy(mainDocumentPart.contents)


            for (jubilar in jubilare) {
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

                mainDocumentPart.variableReplace(placeholders)

                if (jubilar != jubilare.last()) {
                    mainDocumentPart.contents.body.content.addAll(template.body.content)
                }
            }

            // Save the new document
            wordMLPackage.save(File("/Users/leobenz/Downloads/Anschreiben_Jubilare_combined.docx"))
            println("printed!!!")
        }
    }

    fun configureSummerHolidays(start: LocalDate, end: LocalDate) {
        viewModelScope.launch {
            standchenRepository.insert(Holiday(start, end))
        }
    }


    // TODO: File Picker with Settings for the input location with the templates and the output location (folder)
    // TODO: Naming schema for the output files
    // TODO: Print button for individual jubilare, all jubilare for a standchen, all jubilare of a month and a manual date range

//    fun fillPlaceholdersInWordDocument(inputFilePath: String, outputFilePath: String, placeholders: Map<String, String>) {
//        val mainDocumentPart: MainDocumentPart = wordMLPackage.mainDocumentPart
//
//        VariablePrepare.prepare(wordMLPackage)
//        mainDocumentPart.variableReplace(placeholders)
//        wordMLPackage.save(File(outputFilePath))
//    }
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
