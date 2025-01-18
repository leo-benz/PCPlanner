@file:OptIn(ExperimentalUuidApi::class)

package viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import model.*
import repository.JubilareRepository
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class JubilareViewModel(
    private val repository: JubilareRepository
): ViewModel() {

    fun getJubilare(): Flow<List<Jubilar>> {
        return repository.getJubilare()
    }

    fun insertRandomJubilar() {
        val jubilar = generateRandomJubilar()
        repository.insert(jubilar)
    }

    fun filterJubilare(query: String): Flow<List<Jubilar>> {
        return repository.filterJubilare(query)
    }

    private fun generateRandomJubilar(): Jubilar {
        val firstNames = listOf("John", "Jane", "Alex", "Emily", "Chris")
        val lastNames = listOf("Doe", "Smith", "Johnson", "Williams", "Brown")
        val genders = Gender.entries.toTypedArray()
        val classes = listOf(BirthdayJubilar::class, AnniversaryJubilar::class)

        val firstName = firstNames.random()
        val lastName = lastNames.random()
        val birthdate = LocalDate(1970 + Random.nextInt(50), Random.nextInt(1, 12), Random.nextInt(1, 28))
        val gender = genders.random()
        val address = "Random Address ${Random.nextInt(1000)}"
        val optOut = Random.nextBoolean()
        val comment = "Random Comment ${Random.nextInt(1000)}"

        val clazz = classes.random()
        when (clazz) {
            BirthdayJubilar::class -> {
                return BirthdayJubilar(
                    jubilarId = Uuid.random(),
                    firstName = firstName,
                    lastName = lastName,
                    originalJubilarDate = birthdate,
                    gender = gender,
                    address = address,
                    optOut = optOut,
                    comment = comment,
                )
            }
            AnniversaryJubilar::class -> {
                return AnniversaryJubilar(
                    jubilarId = Uuid.random(),
                    lastName = lastName,
                    originalJubilarDate = birthdate,
                    address = address,
                    optOut = optOut,
                    comment = comment,
                )
            }
            else -> {
                throw IllegalArgumentException("Unknown jubilar class")
            }
        }
    }

    fun deleteAllJubilare() {
        repository.deletaAll()
    }

    fun save(jubilar: Jubilar) {
        repository.insert(jubilar)
    }

    fun delete(deletedJubilar: Jubilar) {
        repository.delete(deletedJubilar)
    }
}