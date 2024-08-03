package viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import model.Gender
import model.Jubilar
import model.MarriageAnniversary
import repository.JubilareRepository
import kotlin.random.Random

class JubilareViewModel(
    private val repository: JubilareRepository
): ViewModel() {
    fun greet(): String {
        return repository.greet()
    }

    fun getJubilare(): Flow<List<Jubilar>> {
        return repository.getJubilare()
    }

    fun insertRandomJubilar() {
        val jubilar = generateRandomJubilar()
        repository.insert(jubilar)
    }

    private fun generateRandomJubilar(): Jubilar {
        val firstNames = listOf("John", "Jane", "Alex", "Emily", "Chris")
        val lastNames = listOf("Doe", "Smith", "Johnson", "Williams", "Brown")
        val genders = Gender.values()
        val anniversaries = MarriageAnniversary.values()

        val firstName = firstNames.random()
        val lastName = lastNames.random()
        val birthdate = LocalDate(1970 + Random.nextInt(50), Random.nextInt(1, 12), Random.nextInt(1, 28))
        val gender = genders.random()
        val address = "Random Address ${Random.nextInt(1000)}"
        val optOut = Random.nextBoolean()
        val comment = "Random Comment ${Random.nextInt(1000)}"
        val marriageAnniversary = anniversaries.random()

        return Jubilar(
            firstName = firstName,
            lastName = lastName,
            birthdate = birthdate,
            gender = gender,
            address = address,
            optOut = optOut,
            comment = comment,
            marriageAnniversary = marriageAnniversary
        )
    }
}