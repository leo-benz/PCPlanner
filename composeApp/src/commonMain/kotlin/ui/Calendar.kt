package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import java.time.format.TextStyle
import java.util.*

// Yearly Calendar View
// Highlight Number of Birthdays on each day
// Highlight Number of potential standchen
// Select standchen sonntage

private val LocalDate.daysInMonth: Int
    get() {
        val start = LocalDate(year, monthNumber, 1)
        val end = start.plus(1, DateTimeUnit.MONTH)
        return start.until(end, DateTimeUnit.DAY)
    }

@Composable
fun YearOverview(year: Int) {
    BoxWithConstraints (modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        val minWidth = 110
        val spacing = 16
        val isScrollable = maxWidth < (12 * minWidth + 11 * spacing).dp
        val scrollState = rememberScrollState()
        val modifier = if (isScrollable) {
            Modifier.fillMaxWidth().horizontalScroll(scrollState)
        } else {
            Modifier.fillMaxWidth()
        }

        val verticalScrollState = rememberScrollState()

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(spacing.dp)) {
            val mod = if (isScrollable) {
                Modifier.width(minWidth.dp)
            } else {
                Modifier.weight(1f)
            }
            for (month in Month.entries) {
                MonthOverview(year, month, verticalScrollState, mod)
            }
        }
    }
}

@Composable
fun MonthOverview(year: Int, month: Month, scrollState: ScrollState, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = month.getDisplayName(TextStyle.FULL, Locale.GERMAN),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            DaysOfMonthView(year, month, scrollState)
        }
    }
}

@Composable
fun DaysOfMonthView(year: Int, month: Month, scrollState: ScrollState) {
    val daysInMonth = LocalDate(year, month, 1).daysInMonth

    val layoutModifier =  Modifier.padding(8.dp).fillMaxWidth().height(24.dp)

    Column(
        modifier = Modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (day in 1..daysInMonth) {
            DayCell(day, LocalDate(year, month, day).dayOfWeek, layoutModifier)
        }
        for (i in 0..< 31-daysInMonth) {
            Box(layoutModifier)
        }
    }
}

@Composable
fun DayCell(day: Int, dayOfWeek: DayOfWeek, modifier: Modifier) {
    val bgColor = when (dayOfWeek) {
        DayOfWeek.SUNDAY -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.background
    }
    val fgColor = when (dayOfWeek) {
        DayOfWeek.SUNDAY -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onBackground
    }
    Box(modifier = modifier.background(bgColor), contentAlignment = Alignment.Center) {
        Text(text = day.toString(), Modifier.background(bgColor), color = fgColor)
    }
}
