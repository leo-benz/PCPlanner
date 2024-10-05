package viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import model.*
import org.openapitools.client.models.HolidayResponse
import repository.JubilareRepository
//import org.openapitools.client.apis.HolidaysApi
import repository.StandchenRepository
import java.time.DayOfWeek

class PlanningViewModel(
    private val standchenRepository: StandchenRepository,
    private val jubilareRepository: JubilareRepository
) : ViewModel() {

    var dateDetails: MutableState<LocalDate?> = mutableStateOf(null)

    var year = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year)

    var isInitialized: StateFlow<Boolean> = year.flatMapLatest { year ->
        standchenRepository.getStandchen(year).map { it.isNotEmpty() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    private var cachedHoliday: SharedFlow<HolidayResponse> = year.mapNotNull { year ->
        standchenRepository.getSummerHoliday(year)
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    fun updateYear(newYear: Int) {
        year.value = newYear
    }

    fun generateInitialStandchen() {
        // Create a standchen for every second sunday starting from the first sunday in the year
        // Skip the second sunday in a month
        viewModelScope.launch {
            cachedHoliday.first().let { holiday ->
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
        }
    }

    fun assignJubilareToStandchen() {
        viewModelScope.launch {
            val standchenList = standchenRepository.getStandchen(year.value).first()
            var jubilare = jubilareRepository.getJubilare().first()
            jubilare = jubilare.filter {
                val age = year.value - it.birthdate.year
                var eligible = age == 80 || age >= 85 || it.marriageAnniversary != MarriageAnniversary.NONE

                eligible = true // FIXME: Remove this line to enable the age check

                return@filter eligible
            }
            jubilare = jubilare.sortedBy { LocalDate(year.value, it.birthdate.monthNumber, it.birthdate.dayOfMonth) }
            var currentStandchen = standchenList.first()
            for (ju in jubilare) {
                val currentBirthday = LocalDate(year.value, ju.birthdate.monthNumber, ju.birthdate.dayOfMonth)
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
        return cachedHoliday.map { holiday ->
            day in holiday.startDate..holiday.endDate
        }
    }

    fun isStandchen(day: LocalDate): Flow<Boolean> {
        return standchenRepository.getStandchen(year.value).map { standchenList ->
            standchenList.any { it.date == day }
        }
    }

    fun isJubilarDay(day: LocalDate): Flow<Boolean> {
        return jubilareRepository.getJubilare().map { jubilareList ->
            jubilareList.any { it.birthdate.monthNumber == day.monthNumber && it.birthdate.dayOfMonth == day.dayOfMonth }
        }
    }

    fun onDaySelected(day: LocalDate) {
        viewModelScope.launch {
            println("Day selected: $day")
            println("Standchen: ${standchenRepository.getStandchenWithJubilare(day).first()}")
            println("Jubilare: ${jubilareRepository.getJubilareWithInvites(day).first()}")

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
}

private fun LocalDate.plusDays(i: Int): LocalDate {
    return this.plus(i, DateTimeUnit.DAY)
}
