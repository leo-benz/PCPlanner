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
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import model.AnniversaryJubilar
import model.BirthdayJubilar
import model.GERMAN_FULL
import org.koin.compose.viewmodel.koinViewModel
import viewmodel.PlanningViewModel
import java.time.format.DateTimeFormatter

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
                            Text(if (isStandchen) "Ständchen Absagen" else "Ständchen Spielen")
                        }
                    }
                }

                val jubilare by viewModel.jubilare(date).collectAsState(initial = emptyList())
                if (jubilare.isNotEmpty()) {
                    Text("${jubilare.size} Jubilar${if (jubilare.size == 1) "" else "e"}:", style = MaterialTheme.typography.bodyMedium)
                    jubilare.forEach {
                        when (it) {
                            is BirthdayJubilar -> {
                                Text("${it.firstName} ${it.lastName}", style = MaterialTheme.typography.bodySmall)
                            }
                            is AnniversaryJubilar -> {
                                Text("Ehepaar ${it.lastName}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                val launcher = rememberFileSaverLauncher { result ->
                    if (result != null) {
                        viewModel.print(jubilare, date.year, result.file)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (jubilare.isNotEmpty()) {
                        TextButton(onClick = { launcher.launch(
                            baseName = "Jubilare_${date.format(LocalDate.Format { dayOfMonth(); char('_'); monthNumber(); char('_'); year() })}",
                            extension = "docx",
                            initialDirectory = System.getProperty("user.home")
                        ) }) {
                            Text("Einladungen Speichern")
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