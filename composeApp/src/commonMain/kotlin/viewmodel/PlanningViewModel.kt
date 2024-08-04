package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import model.Standchen
//import org.openapitools.client.apis.HolidaysApi
import repository.StandchenRepository
import java.io.Console
import java.time.DayOfWeek

class PlanningViewModel(
    private val standchenRepository: StandchenRepository
): ViewModel() {

    fun generateInitialStandchen(year: Int) {
        // Create a standchen for every second sunday starting from the first sunday in the year
        // Skip the second sunday in a month
        viewModelScope.launch {
//            val holiday = standchenRepository.getSummerHoliday(year)

            val standchenList = mutableListOf<Standchen>()
            for (month in 1..12) {
                val firstSunday = getFirstSunday(year, month)
                var day = firstSunday
                var week = 1
                while (day.monthNumber == month) {
                    if (week == 2) {
                        day = day.plusDays(7)
                        week = 1
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

    private fun getFirstSunday(year: Int, month: Int): LocalDate {
        var day = LocalDate(year, month, 1)
        while (day.dayOfWeek != DayOfWeek.SUNDAY) {
            day = day.plusDays(1)
        }
        return day
    }

    fun isInitialized(year: Int): Flow<Boolean> {
        return standchenRepository.getStandchen(year).map { it.isNotEmpty() }
    }
}

private fun LocalDate.plusDays(i: Int): LocalDate {
    return this.plus(i, DateTimeUnit.DAY)
}
