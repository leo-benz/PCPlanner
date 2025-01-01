import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate
import model.Holiday

class HolidayTest {

    @Test
    fun holidayInitializationWithValidDates() {
        val startDate = LocalDate(2023, 12, 24)
        val endDate = LocalDate(2023, 12, 31)
        val holiday = Holiday(startDate, endDate)
        assertEquals(2023, holiday.year)
        assertEquals(startDate, holiday.startDate)
        assertEquals(endDate, holiday.endDate)
    }

    @Test
    fun holidayInitializationWithDifferentYears() {
        val startDate = LocalDate(2023, 12, 31)
        val endDate = LocalDate(2024, 1, 1)
        val holiday = Holiday(startDate, endDate)
        assertEquals(2023, holiday.year)
        assertEquals(startDate, holiday.startDate)
        assertEquals(endDate, holiday.endDate)
    }

    @Test
    fun holidayInitializationWithSameStartAndEndDate() {
        val date = LocalDate(2023, 12, 25)
        val holiday = Holiday(date, date)
        assertEquals(2023, holiday.year)
        assertEquals(date, holiday.startDate)
        assertEquals(date, holiday.endDate)
    }

    @Test
    fun holidayInitializationWithLeapYear() {
        val startDate = LocalDate(2024, 2, 28)
        val endDate = LocalDate(2024, 2, 29)
        val holiday = Holiday(startDate, endDate)
        assertEquals(2024, holiday.year)
        assertEquals(startDate, holiday.startDate)
        assertEquals(endDate, holiday.endDate)
    }
}