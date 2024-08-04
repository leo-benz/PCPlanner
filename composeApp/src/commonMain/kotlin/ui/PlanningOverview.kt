@file:OptIn(ExperimentalMaterial3Api::class)

package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
//import androidx.compose.material.icons.filled.ChevronLeft
//import androidx.compose.material.icons.filled.ChevronRight
//import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import viewmodel.PlanningViewModel

@Composable
fun PlanningOverview() {
    val viewModel = koinViewModel<PlanningViewModel>()
    val year by viewModel.year.collectAsState()
    val isInitialized by viewModel.isInitialized.collectAsState(initial = false)

    Scaffold(
        topBar = { CenterAlignedTopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Jahresübersicht")
                    YearPicker(year) { newYear -> viewModel.updateYear(newYear) }
                }

            },
            actions = {
                if (!isInitialized) {
                    IconButton(onClick = { viewModel.generateInitialStandchen() }) {
                        Icon(Icons.Default.Add, contentDescription = "Generate Standchen")
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
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Year")
        }
        TextField(
            value = year.toString(),
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let { onYearChange(it) }
            },
            modifier = Modifier.width(100.dp)
        )
        IconButton(onClick = { onYearChange(year + 1) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward,  contentDescription = "Next Year")
        }
    }
}