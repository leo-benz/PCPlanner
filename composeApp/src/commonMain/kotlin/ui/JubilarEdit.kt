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
    modifier: Modifier = Modifier,
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

    var address by remember(jubilar) { mutableStateOf(jubilar.address) }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
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
                label = { Text("Vorname") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        TextField(
            value = lastName,
            onValueChange = {
                lastName = it
                onJubilarUpdate(
                    jubilar.toEntity().copy(lastName = lastName).toDomain()
                )
            },
            label = { Text("Nachname") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(16.dp))

        // We'll keep the displayed date in a separate var so we can show partial input even if invalid
        val dateFormat = LocalDate.Format {
            dayOfMonth(Padding.ZERO);char('.');monthNumber(Padding.ZERO);char('.');year()
        }
        var dateText by remember(jubilar) {
            mutableStateOf(jubilar.originalJubilarDate.format(dateFormat))
        }

        // Track whether the date is currently valid
        var dateError by remember { mutableStateOf<String?>(null) }


        TextField(
            value = dateText,
            onValueChange = { newText ->
                // Optionally, allow only digits and '.' in input
                val filteredText = newText.filter { it.isDigit() || it == '.' }

                // Attempt to parse
                val parsedDate = runCatching {
                    LocalDate.parse(filteredText, dateFormat)
                }.getOrNull()

                if (parsedDate == null && filteredText.isNotEmpty()) {
                    // Mark as error if it's not an empty string
                    dateError = "Datum in dd.MM.yyyy Format eingeben"
                } else {
                    dateError = null
                    // If parsed, update the domain
                    if (parsedDate != null) {
                        onJubilarUpdate(
                            jubilar.toEntity().copy(
                                originalJubilarDate = parsedDate
                            ).toDomain()
                        )
                    }
                }
                dateText = filteredText
            },
            label = { Text(if (jubilar is BirthdayJubilar) "Geburtstag" else "Hochzeitstag") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = (dateError != null),
            supportingText = {
                if (dateError != null) {
                    Text(dateError!!)
                }
            },
        )
        if (jubilar is BirthdayJubilar) {
            ExposedDropdownMenuBox(
                expanded = genderDropDownExpanded,
                onExpandedChange = { genderDropDownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            )
            {

                TextField(
                    value = when (gender) {
                        Gender.MALE -> "Männlich"
                        Gender.FEMALE -> "Weiblich"
                        else -> "N/A"
                    }, // Display the current gender
                    onValueChange = {}, // TextField is read-only; changes happen through dropdown
                    label = { Text("Geschlecht") },
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
                            text = { Text( when (genderOption) {
                                Gender.MALE -> "Männlich"
                                Gender.FEMALE -> "Weiblich"
                                else -> "N/A"
                            }) },
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
            Spacer(modifier = Modifier.height(16.dp))
        }

        TextField(
            value = address,
            onValueChange = {
                address = it
                onJubilarUpdate(
                    jubilar.toEntity().copy(address = address).toDomain()
                )
            },
            label = { Text("Addresse") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}