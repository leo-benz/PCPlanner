package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import model.GERMAN_FULL
import org.koin.compose.viewmodel.koinViewModel
import viewmodel.PlanningViewModel

@Composable
fun DateDetails(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<PlanningViewModel>()

    if (viewModel.dateDetails.value != null) {
    Dialog(
        onDismissRequest = { viewModel.dismissDateDetails() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true))
    {

        val date = viewModel.dateDetails.value!!

        val isStandchen by viewModel.isStandchen(date).collectAsState(initial = false)

        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = date.format(LocalDate.Format { dayOfWeek(DayOfWeekNames.GERMAN_FULL); chars(", "); dayOfMonth(); chars(". "); monthName(
                    MonthNames.GERMAN_FULL
                ) }), style = MaterialTheme.typography.titleLarge)
                if (date.dayOfWeek == DayOfWeek.SUNDAY) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        val text = if (isStandchen) "Ständchensonntag" else "Sonntag"
                        Text(text = text, style = MaterialTheme.typography.bodyMedium)

                        TextButton(onClick = { viewModel.toggleStandchen(date) }) {
                            Text(if (isStandchen) "Absagen" else "Ständchen Spielen")
                        }
                    }
                }

                val jubilare by viewModel.jubilare(date).collectAsState(initial = emptyList())
                if (jubilare.isNotEmpty()) {
                    Text("${jubilare.size} Jubilar${if (jubilare.size == 1) "" else "e"}:", style = MaterialTheme.typography.bodyMedium)
                    jubilare.forEach {
                        Text("${it.firstName} ${it.lastName}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (jubilare.isNotEmpty()) {
                        TextButton(onClick = { viewModel.print(jubilare.first(), date.year) }) {
                            Text("Print")
                        }
                    }
                    TextButton(onClick = { viewModel.dismissDateDetails() }) {
                        Text("Schließen")
                    }
                }
            }
        }
    }
    }
}