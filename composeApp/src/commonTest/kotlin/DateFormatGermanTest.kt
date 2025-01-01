import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import model.GERMAN_FULL
import kotlin.test.Test
import kotlin.test.assertEquals

class DateFormatGermanTest {

    @Test
    fun monthNamesGermanFullContainsCorrectNames() {
        val expected = listOf(
            "Januar", "Februar", "März", "April", "Mai", "Juni",
            "Juli", "August", "September", "Oktober", "November", "Dezember"
        )
        assertEquals(expected, MonthNames.GERMAN_FULL.names)
    }

    @Test
    fun dayOfWeekNamesGermanFullContainsCorrectNames() {
        val expected = listOf(
            "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"
        )
        assertEquals(expected, DayOfWeekNames.GERMAN_FULL.names)
    }

    @Test
    fun monthNamesGermanFullHasTwelveMonths() {
        assertEquals(12, MonthNames.GERMAN_FULL.names.size)
    }

    @Test
    fun dayOfWeekNamesGermanFullHasSevenDays() {
        assertEquals(7, DayOfWeekNames.GERMAN_FULL.names.size)
    }
}