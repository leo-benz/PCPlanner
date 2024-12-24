@file:OptIn(ExperimentalUuidApi::class)

package model

import androidx.room.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(inheritSuperIndices = true)
open class Jubilar  constructor(
    @PrimaryKey val jubilarId: Uuid = Uuid.random(),
    val lastName: String,
    val originalJubilarDate: LocalDate,
    val address: String,
    val optOut: Boolean,
    val comment: String,
)

@Entity
class BirthdayJubilar (
    jubilarId: Uuid = Uuid.random(),
    lastName: String,
    originalJubilarDate: LocalDate,
    address: String,
    optOut: Boolean,
    comment: String,
    val firstName: String,
    var gender: Gender
) : Jubilar(jubilarId, lastName, originalJubilarDate, address, optOut, comment)

@Entity
class AnniversaryJubilar(
    jubilarId: Uuid = Uuid.random(),
    lastName: String,
    originalJubilarDate: LocalDate,
    address: String,
    optOut: Boolean,
    comment: String,
) : Jubilar(jubilarId, lastName, originalJubilarDate, address, optOut, comment) {
    fun marriageAnniversary(year: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year): MarriageAnniversary {
        val years = year - originalJubilarDate.year
        return when (years) {
            70 -> MarriageAnniversary.PLATINUM
            65 -> MarriageAnniversary.IRON
            60 -> MarriageAnniversary.DIAMOND
            50 -> MarriageAnniversary.GOLDEN
            else -> MarriageAnniversary.NONE
        }
    }
}

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

class UuidConverter {
    @TypeConverter
    fun fromUuid(uuid: Uuid): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUuid(uuid: String): Uuid {
        return Uuid.parse(uuid)
    }
}