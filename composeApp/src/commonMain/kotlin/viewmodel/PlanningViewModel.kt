package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import model.Standchen
import org.openapitools.client.models.HolidayResponse
//import org.openapitools.client.apis.HolidaysApi
import repository.StandchenRepository
import java.io.Console
import java.time.DayOfWeek

class PlanningViewModel(
    private val standchenRepository: StandchenRepository
) : ViewModel() {

    init {

    }

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
                for (month in 1..12) {
                    val firstSunday = getFirstSunday(year.value, month)
                    var day = firstSunday
                    var week = 1
                    while (day.monthNumber == month) {
                        if (week == 2) {
                            day = day.plusDays(7)
                            week = 1
                            continue
                        }
                        if (day in holiday.startDate..holiday.endDate) {
                            day = day.plusDays(7)
                            continue
                        }
                        standchenList.add(Standchen(day, false))
                        day = day.plusDays(7)
                        week++
                    }
                }
                standchenRepository.insert(standchenList)
            }
        }
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
}

private fun LocalDate.plusDays(i: Int): LocalDate {
    return this.plus(i, DateTimeUnit.DAY)
}
