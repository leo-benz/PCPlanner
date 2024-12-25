@file:OptIn(ExperimentalUuidApi::class)

package model

import androidx.room.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@Entity(indices = [
    Index(value = ["type", "lastName", "firstName", "originalJubilarDate", "gender"], unique = true),
])
data class JubilarEntity(
    @Contextual @PrimaryKey val jubilarId: Uuid = Uuid.random(),
    val type: JubilarType,              // Distinguish BIRTHDAY vs. ANNIVERSARY
    val lastName: String,
    val originalJubilarDate: LocalDate,
    val address: String,
    val optOut: Boolean,
    val comment: String,

    // Fields specific to "BirthdayJubilar" – can be null for Anniversary
    val firstName: String? = null,
    val gender: Gender? = null
)

/** e.g. an enum to store which "subclass" it represents */
enum class JubilarType {
    BIRTHDAY,
    ANNIVERSARY
}

@Serializable
sealed class Jubilar {
    abstract val jubilarId: Uuid
    abstract val lastName: String
    abstract val originalJubilarDate: LocalDate
    abstract val address: String
    abstract val optOut: Boolean
    abstract val comment: String
}

data class BirthdayJubilar(
    @Contextual override val jubilarId: Uuid = Uuid.random(),
    override val lastName: String,
    override val originalJubilarDate: LocalDate,
    override val address: String,
    override val optOut: Boolean = false,
    override val comment: String = "",
    val firstName: String,
    val gender: Gender
) : Jubilar()

data class AnniversaryJubilar(
    @Contextual override val jubilarId: Uuid = Uuid.random(),
    override val lastName: String,
    override val originalJubilarDate: LocalDate,
    override val address: String,
    override val optOut: Boolean = false,
    override val comment: String = ""
) : Jubilar() {
    fun marriageAnniversary(year: Int  = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year): MarriageAnniversary {
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

fun JubilarEntity.toDomain(): Jubilar {
    return when (type) {
        JubilarType.BIRTHDAY -> BirthdayJubilar(
            jubilarId = jubilarId,
            lastName = lastName,
            originalJubilarDate = originalJubilarDate,
            address = address,
            optOut = optOut,
            comment = comment,
            firstName = this.firstName ?: "",
            gender = this.gender ?: Gender.OTHER // or some default
        )
        JubilarType.ANNIVERSARY -> AnniversaryJubilar(
            jubilarId = jubilarId,
            lastName = lastName,
            originalJubilarDate = originalJubilarDate,
            address = address,
            optOut = optOut,
            comment = comment
        )
    }
}

fun Jubilar.toEntity(): JubilarEntity {
    return when (this) {
        is BirthdayJubilar -> JubilarEntity(
            jubilarId = jubilarId,
            type = JubilarType.BIRTHDAY,
            lastName = lastName,
            originalJubilarDate = originalJubilarDate,
            address = address,
            optOut = optOut,
            comment = comment,
            firstName = firstName,
            gender = gender
        )
        is AnniversaryJubilar -> JubilarEntity(
            jubilarId = jubilarId,
            type = JubilarType.ANNIVERSARY,
            lastName = lastName,
            originalJubilarDate = originalJubilarDate,
            address = address,
            optOut = optOut,
            comment = comment
        )
    }
}

data class JubilarWithInvites (
    @Embedded val jubilar: JubilarEntity,
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