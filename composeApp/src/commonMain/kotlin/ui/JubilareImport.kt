package ui

import ImportScreen
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import viewmodel.ImportViewModel

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun JubilareImport(navigateBack: () -> Unit = {}) {
    val viewModel = koinViewModel<ImportViewModel>()

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text("Jubilare Importieren")
            }, navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ){
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            val launcher = rememberFilePickerLauncher(
                type = PickerType.Image,
                mode = PickerMode.Single,
                title = "Bild auswählen",
                initialDirectory = System.getProperty("user.home")
            ) { file ->
                if (file != null) {
                    viewModel.import(file)
                }
            }

            Button(onClick = { launcher.launch() }) {
                Text("Bild auswählen")
            }

            ImportScreen(viewModel.file, viewModel.jubilareState, viewModel::updateJubilar)
        }
    }
}