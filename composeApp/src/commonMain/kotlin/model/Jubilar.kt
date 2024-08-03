package model

import androidx.room.Entity
import kotlinx.datetime.LocalDate

@Entity(primaryKeys = ["firstName", "lastName", "birthdate"])
data class Jubilar(
    val firstName: String,
    val lastName: String,
    var gender: Gender,
    val birthdate: LocalDate,
    val address: String,
    val optOut: Boolean,
    val comment: String,
    val marriageAnniversary: MarriageAnniversary
)

enum class Gender {
    MALE, FEMALE, OTHER
}

enum class MarriageAnniversary {
    NONE, GOLDEN, DIAMOND, IRON, PLATINUM
}