package model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.datetime.LocalDate

@Entity()
data class Jubilar(
    @PrimaryKey(autoGenerate = true) val jubilarId: Int,
    val firstName: String,
    val lastName: String,
    var gender: Gender,
    val birthdate: LocalDate,
    val address: String,
    val optOut: Boolean,
    val comment: String,
    val marriageAnniversary: MarriageAnniversary,
)

data class JubilarWithInvites (
    @Embedded val jubilar: Jubilar,
    @Relation(
        entity = StandchenInvite::class,
        parentColumn = "jubilarId",
        entityColumn = "jubilarId"
    )
    val invites: List<StanchenInviteWithStandchen>
)

enum class Gender {
    MALE, FEMALE, OTHER
}

enum class MarriageAnniversary {
    NONE, GOLDEN, DIAMOND, IRON, PLATINUM
}