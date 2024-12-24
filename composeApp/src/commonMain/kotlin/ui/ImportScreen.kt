@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import model.*
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import viewmodel.ImportViewModel
import java.io.File
import javax.imageio.ImageIO
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, KoinExperimentalAPI::class)
@Composable
fun ImportScreen(
    imagePath: StateFlow<File?>,
    jubilareState: StateFlow<List<Jubilar>>,
    onJubilarUpdate: (Int, Jubilar) -> Unit
) {
    val viewModel = koinViewModel<ImportViewModel>()

    val jubilareList by jubilareState.collectAsState()
    var currentIndex by remember { mutableStateOf(0) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    var genderDropDownExpanded by remember { mutableStateOf(false) } // To manage dropdown visibility
    val genders = Gender.entries.toTypedArray() // List of genders to display

    // Load the image bitmap from the file
    LaunchedEffect(Unit) {
        imagePath.collect { file ->
            file?.let {
                try {
                    val bufferedImage = ImageIO.read(it)
                    imageBitmap = bufferedImage.toComposeImageBitmap()
                } catch (e: Exception) {
                    e.printStackTrace()
                    imageBitmap = null
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Display the imported image
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = "Imported Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        "No Image Available",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }


            // Editable Jubilar info
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (jubilareList.isNotEmpty()) {
                    val jubilar = jubilareList[currentIndex]
                    var lastName by remember(jubilar) { mutableStateOf(jubilar.lastName) }
                    var gender by remember(jubilar) {
                        if (jubilar is BirthdayJubilar) mutableStateOf(jubilar.gender) else mutableStateOf(
                            Gender.OTHER)
                    }
                    var firstName by remember(jubilar) {
                        if (jubilar is BirthdayJubilar) mutableStateOf(jubilar.firstName) else mutableStateOf(
                            ""
                        )
                    }
                    val dateFormat = LocalDate.Format {
                        dayOfMonth(Padding.ZERO);char('.');monthNumber(Padding.ZERO);char('.');year()
                    }
                    var originalJubilarDate by remember(jubilar) { mutableStateOf(jubilar.originalJubilarDate.format(dateFormat)) }
                    var address by remember(jubilar) { mutableStateOf(jubilar.address) }

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (jubilar is BirthdayJubilar) {
                            TextField(
                                value = firstName,
                                onValueChange = {
                                    firstName = it
                                    onJubilarUpdate(
                                        currentIndex,
                                        jubilar.toEntity().copy(firstName = firstName).toDomain()
                                    )
                                },
                                label = { Text("First Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        TextField(
                            value = lastName,
                            onValueChange = {
                                lastName = it
                                onJubilarUpdate(
                                    currentIndex,
                                    jubilar.toEntity().copy(lastName = lastName).toDomain()
                                )
                            },
                            label = { Text("Last Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        TextField(
                            value = originalJubilarDate,
                            onValueChange = {
                                originalJubilarDate = it
                                // Validate and update the date
                                onJubilarUpdate(
                                    currentIndex,
                                    jubilar.toEntity().copy(originalJubilarDate = kotlin.runCatching {
                                        LocalDate.parse(it, dateFormat)
                                    }.getOrDefault(jubilar.originalJubilarDate)).toDomain()
                                )
                            },
                            label = { Text(if (jubilar is BirthdayJubilar) "Birthdate" else "Hochzeitstag") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (jubilar is BirthdayJubilar) {
                            ExposedDropdownMenuBox(
                                expanded = genderDropDownExpanded,
                                onExpandedChange = { genderDropDownExpanded = it},
                                modifier = Modifier.fillMaxWidth())
                            {

                                TextField(
                                    value = gender.name, // Display the current gender
                                    onValueChange = {}, // TextField is read-only; changes happen through dropdown
                                    label = { Text("Gender") },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderDropDownExpanded) },
                                    readOnly = true, // Prevent manual editing
                                )

                                ExposedDropdownMenu(
                                    expanded = genderDropDownExpanded,
                                    onDismissRequest = { genderDropDownExpanded = false } // Close dropdown when clicked outside
                                ) {
                                    genders.forEach { genderOption ->
                                        DropdownMenuItem(
                                            text = { Text(genderOption.name) },
                                            onClick = {
                                                gender = genderOption
                                                genderDropDownExpanded = false // Close dropdown after selection
                                                onJubilarUpdate(
                                                    currentIndex,
                                                    jubilar.toEntity().copy(gender = genderOption).toDomain()
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        TextField(
                            value = address,
                            onValueChange = {
                                address = it
                                onJubilarUpdate(
                                    currentIndex,
                                    jubilar.toEntity().copy(address = address).toDomain()
                                )
                            },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Text(
                        "No Jubilars Available",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (currentIndex > 0) currentIndex -= 1
                },
                enabled = currentIndex > 0
            ) {
                Text("Zurück")
            }

            Button(
                onClick = {
                    viewModel.insertEmptyJubilar(currentIndex)
                }
            ) {
                Text("Neu")
            }

            Button(
                onClick = {
                    if (currentIndex < jubilareList.size - 1) currentIndex += 1
                },
                enabled = currentIndex < jubilareList.size - 1
            ) {
                Text("Weiter")
            }

            val isStored by viewModel.isJubilarStored(currentIndex).collectAsState(initial = false)
            if (!isStored) {
                Button(
                    onClick = {
                        viewModel.saveJubilar(currentIndex)
                        if (currentIndex < jubilareList.size - 1) currentIndex += 1
                    },
                    enabled = jubilareList.isNotEmpty(),
                ) {
                    Text("Speichern")
                }
            } else {
                Button(
                    onClick = {
                        viewModel.delete(currentIndex)
                    },
                    enabled = jubilareList.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Löschen")
                }
            }
        }
    }
}

