@file:OptIn(ExperimentalMaterial3Api::class)

package ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import model.AnniversaryJubilar
import model.BirthdayJubilar
import model.Jubilar
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import viewmodel.JubilareViewModel

@OptIn(KoinExperimentalAPI::class)
@Composable
fun JubilareOverview(navigateBack: () -> Unit = {}) {
    val viewModel = koinViewModel<JubilareViewModel>()
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text("Jubilare")
        }, navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        })
    }) {
        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { viewModel.insertRandomJubilar() }) {
                    Text("Neuer Geburtstag")
                }

                Button(onClick = { viewModel.insertRandomJubilar() }) {
                    Text("Neuer Hochzeitstag")
                }

//                Button(onClick = { viewModel.deleteAllJubilare() }) {
//                    Text("Alle Löschen")
//                }
            }
            val jubilare by viewModel.getJubilare().collectAsState(initial = emptyList())
            JubilarTable(jubilare)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JubilarTable(jubilare: List<Jubilar>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stickyHeader {
            JubilarTableHeader()
        }
        items(jubilare) { jubilar ->
            JubilarRow(jubilar)
        }
    }
}

@Composable
fun JubilarTableHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.background).padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Name", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground)
        Text(text = "Geschlecht", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground)
        Text(text = "Geburtstag", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground)
        Text(text = "Adresse", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground)
        Text(text = "Opt Out", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground)
        Text(text = "Kommentar", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground)
        Text(text = "Hochzeitstag", modifier = Modifier.weight(1f), color = MaterialTheme.colors.onBackground)
    }
}

@Composable
fun JubilarRow(jubilar: Jubilar) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (jubilar is BirthdayJubilar) {
            Text(text = "${jubilar.firstName} ${jubilar.lastName}", modifier = Modifier.weight(1f))
            Text(text = "${jubilar.gender}", modifier = Modifier.weight(1f))
        } else {
            Text(text = "Ehepaar ${jubilar.lastName}", modifier = Modifier.weight(1f))
            Text(text = "", modifier = Modifier.weight(1f))
        }
        Text(text = "${jubilar.originalJubilarDate}", modifier = Modifier.weight(1f))
        Text(text = jubilar.address, modifier = Modifier.weight(1f))
        Text(text = "${jubilar.optOut}", modifier = Modifier.weight(1f))
        Text(text = jubilar.comment, modifier = Modifier.weight(1f))
        if (jubilar is AnniversaryJubilar) {
            Text(text = "${jubilar.marriageAnniversary()}", modifier = Modifier.weight(1f))
        } else {
            Text(text = "", modifier = Modifier.weight(1f))
        }
    }

}