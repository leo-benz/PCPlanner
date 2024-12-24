@file:OptIn(ExperimentalMaterial3Api::class)

package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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
import model.Holiday
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import viewmodel.JubilareViewModel
import viewmodel.PlanningViewModel

@OptIn(KoinExperimentalAPI::class)
@Composable
fun PlanningOverview(navigateBack: () -> Unit = {}) {
    val viewModel = koinViewModel<PlanningViewModel>()
    val jubilareViewModel = koinViewModel<JubilareViewModel>()

    val year by viewModel.year.collectAsState()
    val isInitialized by viewModel.isInitialized.collectAsState(initial = false)

    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showHolidayDialog by remember { mutableStateOf(false) }
    val currentHoliday by viewModel.holiday.collectAsState(initial = null)

    // Whenever the errorMessage changes, show it as a Snackbar
    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrEmpty()) {
            snackbarHostState.showSnackbar(errorMessage!!)
            // Reset the error so it doesn't keep reappearing
            viewModel.errorMessage.value = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { CenterAlignedTopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Jahresübersicht")
                    YearPicker(year) { newYear -> viewModel.updateYear(newYear) }
                }

            },
            actions = {
                IconButton(onClick = {
                    jubilareViewModel.insertRandomJubilar()
                }) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Jubilar")
                }
                IconButton(onClick = { showHolidayDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Configure Summer Holidays"
                    )
                }
                if (!isInitialized) {
                    IconButton(onClick = { viewModel.generateInitialStandchen() }) {
                        Icon(Icons.Default.Add, contentDescription = "Generate Standchen")
                    }
                } else {
                    IconButton(onClick = { viewModel.assignJubilareToStandchen() }) {
                        Icon(Icons.Default.Call, contentDescription = "Assign Standchen")
                    }
                }
            }, navigationIcon = {
                androidx.compose.material.IconButton(onClick = navigateBack) {
                    androidx.compose.material.Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        ) }
    ) {
        if (showHolidayDialog) {
            ConfigureSummerHolidayDialog(
                currentHoliday = currentHoliday,
                onDismiss = { showHolidayDialog = false },
                onSave = { start, end ->
                    viewModel.configureSummerHolidays(start, end)
                    showHolidayDialog = false
                }
            )
        }

        YearOverview(year)
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

@Composable
fun ConfigureSummerHolidayDialog(
    currentHoliday: Holiday?,
    onDismiss: () -> Unit,
    onSave: (LocalDate, LocalDate) -> Unit
) {
    // Using basic text fields for date input here for simplicity
    val initialStart = currentHoliday?.startDate?.toString() ?: ""
    val initialEnd   = currentHoliday?.endDate?.toString() ?: ""

    var startDateText by remember { mutableStateOf(initialStart) }
    var endDateText   by remember { mutableStateOf(initialEnd) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure Summer Holidays") },
        text = {
            Column {
                OutlinedTextField(
                    value = startDateText,
                    onValueChange = { startDateText = it },
                    label = { Text("Start Date (YYYY-MM-DD)") }
                )
                OutlinedTextField(
                    value = endDateText,
                    onValueChange = { endDateText = it },
                    label = { Text("End Date (YYYY-MM-DD)") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                // Convert user input into LocalDate
                // In real code, be sure to handle invalid date formats or empty strings
                val start = LocalDate.parse(startDateText)
                val end = LocalDate.parse(endDateText)
                onSave(start, end)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}