@file:OptIn(ExperimentalMaterial3Api::class)

package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import viewmodel.PlanningViewModel

@Composable
fun PlanningOverview() {
    var year by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year) }
    val viewModel = koinViewModel<PlanningViewModel>()
    val isInitialized by viewModel.isInitialized(year).collectAsState(initial = false)

    Scaffold(
        topBar = { CenterAlignedTopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Jahresübersicht")
                    YearPicker(year) { newYear -> year = newYear }
                }

            },
            actions = {
                if (!isInitialized) {
                    IconButton(onClick = { viewModel.generateInitialStandchen(year) }) {
                        Icon(Icons.Outlined.PendingActions, contentDescription = "Generate Standchen")
                    }
                }
            }
        ) }
    ) {
        YearOverview(year, emptyList()) { }
    }
}

@Composable
fun YearPicker(
    year: Int,
    onYearChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = { onYearChange(year - 1) }) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Year")
        }
        TextField(
            value = year.toString(),
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let { onYearChange(it) }
            },
            modifier = Modifier.width(100.dp)
        )
        IconButton(onClick = { onYearChange(year + 1) }) {
            Icon(Icons.Default.ChevronRight,  contentDescription = "Next Year")
        }
    }
}