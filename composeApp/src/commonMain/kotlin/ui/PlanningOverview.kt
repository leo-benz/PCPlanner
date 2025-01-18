@file:OptIn(ExperimentalMaterial3Api::class)

package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.char
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
//    val isInitialized by viewModel.isInitialized.collectAsState(initial = false)

    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showHolidayDialog by remember { mutableStateOf(false) }
    val currentHoliday by viewModel.holiday.collectAsState(initial = null)

    val launcher = rememberFileSaverLauncher { result ->
        if (result != null) {
            viewModel.exportStandchenCsv(year, result.file)
        }
    }

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
                    launcher.launch(
                        baseName = "Jubilare_${year}",
                        extension = "csv",
                        initialDirectory = System.getProperty("user.home")
                    )
                }) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Export Standchen")
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