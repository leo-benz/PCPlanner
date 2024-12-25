package ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import model.*
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)
@Composable
fun JubilarEdit(
    jubilar: Jubilar,
    onJubilarUpdate: (Jubilar) -> Unit
) {
    var lastName by remember(jubilar) { mutableStateOf(jubilar.lastName) }
    var genderDropDownExpanded by remember { mutableStateOf(false) } // To manage dropdown visibility
    val genders = Gender.entries.toTypedArray() // List of genders to display

    var gender by remember(jubilar) {
        if (jubilar is BirthdayJubilar) mutableStateOf(jubilar.gender) else mutableStateOf(
            Gender.OTHER
        )
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
                onExpandedChange = { genderDropDownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            )
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
                    jubilar.toEntity().copy(address = address).toDomain()
                )
            },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}