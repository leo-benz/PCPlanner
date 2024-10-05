package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import model.Jubilar
import model.Standchen
import org.koin.compose.viewmodel.koinViewModel
import viewmodel.JubilareViewModel
import viewmodel.PlanningViewModel
import java.time.format.TextStyle
import java.util.*

// Yearly Calendar View
// Highlight Number of Birthdays on each day
// Highlight Number of potential standchen
// Select standchen sonntage
// Show details for standchen or other date
// Print monthly invites

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

    DateDetails()
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
            DayCell(LocalDate(year, month, day), layoutModifier)
        }
        for (i in 0..< 31-daysInMonth) {
            Box(layoutModifier)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DayCell(day: LocalDate, modifier: Modifier) {
    val viewModel = koinViewModel<PlanningViewModel>()

    val dayOfWeek = day.dayOfWeek

    val isHoliday by viewModel.isHoliday(day).collectAsState(initial = false)
    val isStandchen by viewModel.isStandchen(day).collectAsState(initial = false)
    val isJubilarDay by viewModel.isJubilarDay(day).collectAsState(initial = false)

    var bgColor = when (dayOfWeek) {
        DayOfWeek.SUNDAY -> if (isStandchen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.background
    }

    var fgColor = when (dayOfWeek) {
        DayOfWeek.SUNDAY -> if (isStandchen) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onBackground
    }

    if (isHoliday) {
        bgColor = bgColor.darken(0.9f)
    }

    if (isJubilarDay) {
        bgColor = bgColor.darken(0.7f)
    }

    Box(modifier = modifier.background(bgColor).clickable {
        viewModel.onDaySelected(day)
    }, contentAlignment = Alignment.Center) {
        Text(text = day.dayOfMonth.toString(), Modifier.background(bgColor), color = fgColor)
    }
}

private fun Color.lighen(fl: Float): Color {
    return Color(red = red + (1 - red) * fl, green = green + (1 - green) * fl, blue = blue + (1 - blue) * fl, alpha = alpha)
}

private fun Color.darken(fl: Float): Color {
    return Color(red = red * fl, green = green * fl, blue = blue * fl, alpha = alpha)
}
