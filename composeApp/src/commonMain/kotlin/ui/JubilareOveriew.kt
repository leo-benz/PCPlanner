@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)

package ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.*
import kotlinx.datetime.format.char
import model.*
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import viewmodel.JubilareViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(KoinExperimentalAPI::class)
@Composable
fun JubilareOverview(navigateBack: () -> Unit = {}) {
    val viewModel = koinViewModel<JubilareViewModel>()

    var showJubilarDialog by remember { mutableStateOf(false) }
    var selectedJubilar by remember { mutableStateOf<Jubilar?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text("Jubilare Übersicht")
        }, navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        })
    }) {
        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Suche") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = {
                        selectedJubilar = BirthdayJubilar(
                            lastName = "",
                            firstName = "",
                            address = "",
                            originalJubilarDate = LocalDate(1900, 1, 1),
                            jubilarId = Uuid.random(),
                            gender = Gender.MALE, // Default
                        )  // indicates "create" mode
                        showJubilarDialog = true
                    }) {
                        Text("Neuer Geburtstags Jubilar")
                    }

                    Button(onClick = {
                        selectedJubilar = AnniversaryJubilar(
                            lastName = "",
                            address = "",
                            originalJubilarDate = LocalDate(1900, 1, 1),
                            jubilarId = Uuid.random(),
                        )  // indicates "create" mode
                        showJubilarDialog = true
                    }) {
                        Text("Neuer Ehe Jubilar")
                    }
                }
            }
//            val jubilare by viewModel.getJubilare().collectAsState(initial = emptyList())
            val filteredJubilare by viewModel.filterJubilare(searchQuery).collectAsState(initial = emptyList())

            JubilarTable(filteredJubilare, onEditClick = {
                selectedJubilar = it
                showJubilarDialog = true
            })
        }

        // Show dialog overlay if needed
        if (showJubilarDialog) {
            EditJubilarDialog(
                initialJubilar = selectedJubilar!!,
                onDismiss = { showJubilarDialog = false },
                onSave = { editedJubilar ->
                    viewModel.save(editedJubilar)
                    showJubilarDialog = false
                },
                onDelete = { deletedJubilar ->
                    viewModel.delete(deletedJubilar)
                    showJubilarDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JubilarTable(jubilare: List<Jubilar>, onEditClick: (Jubilar) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stickyHeader {
            JubilarTableHeader()
        }
        items(jubilare) { jubilar ->
            JubilarRow(jubilar, onEditClick)
        }
    }
}

@Composable
fun JubilarTableHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.background).padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Name", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground,  fontWeight = FontWeight.Bold)
        Text(text = "Geschlecht", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground,  fontWeight = FontWeight.Bold)
        Text(text = "Jubilar Datum", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground,  fontWeight = FontWeight.Bold)
        Text(text = "Adresse", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground,  fontWeight = FontWeight.Bold)
//        Text(text = "Opt Out", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground)
//        Text(text = "Kommentar", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground)
        Text(text = "Hochzeitstag (${Clock.System. now().toLocalDateTime(TimeZone. currentSystemDefault()).year})", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground,  fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JubilarRow(jubilar: Jubilar, onEditClick: (Jubilar) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp).onClick{onEditClick(jubilar)},
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (jubilar is BirthdayJubilar) {
            Text(text = "${jubilar.firstName} ${jubilar.lastName}", modifier = Modifier.weight(1f))
            Text(text = when (jubilar.gender) {
                Gender.MALE -> "Männlich"
                Gender.FEMALE -> "Weiblich"
                else -> "N/A"
            }, modifier = Modifier.weight(1f))
        } else {
            Text(text = "Ehepaar ${jubilar.lastName}", modifier = Modifier.weight(1f))
            Text(text = "", modifier = Modifier.weight(1f))
        }
        Text(text = jubilar.originalJubilarDate.format(LocalDate.Format { dayOfMonth(); char('.'); monthNumber(); char('.'); year() }), modifier = Modifier.weight(1f))
        Text(text = jubilar.address, modifier = Modifier.weight(1f))
//        Text(text = "${jubilar.optOut}", modifier = Modifier.weight(1f))
//        Text(text = jubilar.comment, modifier = Modifier.weight(1f))
        if (jubilar is AnniversaryJubilar) {
            Text(text = "${jubilar.marriageAnniversary()}", modifier = Modifier.weight(1f))
        } else {
            Text(text = "", modifier = Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun EditJubilarDialog(
    initialJubilar: Jubilar,
    onDismiss: () -> Unit,
    onSave: (Jubilar) -> Unit,
    onDelete: (Jubilar) -> Unit
) {

    var jubilar by remember { mutableStateOf(initialJubilar)}


    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            elevation = 8.dp
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = if (initialJubilar == null) "Neuer Jubilar" else "Jubilar bearbeiten")

               JubilarEdit(jubilar = jubilar, onJubilarUpdate = { jubilar = it })

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left-aligned "Löschen"
                    TextButton(
                        onClick = { onDelete(jubilar) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error)
                    ) {
                        Text("Löschen")
                    }

                    // Right-aligned "Abbrechen" and "Speichern"
                    Row {
                        TextButton(onClick = onDismiss) {
                            Text("Abbrechen")
                        }
                        TextButton(onClick = { onSave(jubilar) }) {
                            Text("Speichern")
                        }
                    }
                }
            }
        }
    }
}