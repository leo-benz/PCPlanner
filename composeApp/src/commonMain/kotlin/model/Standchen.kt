package model

import androidx.room.*
import kotlinx.datetime.LocalDate
import kotlin.uuid.Uuid

@Entity
data class Standchen (
    @PrimaryKey val date: LocalDate,
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Standchen::class,
            parentColumns = ["date"],
            childColumns = ["date"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Jubilar::class,
            parentColumns = ["jubilarId"],
            childColumns = ["jubilarId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["year", "jubilarId"], unique = true),
    ])
data class StandchenInvite (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val accepted: Boolean,
    val date: LocalDate,
    val jubilarId: Uuid,
    val year: Int = date.year
)

data class StanchenInviteWithStandchen (
    @Embedded val invite: StandchenInvite,
    @Relation(
        parentColumn = "date",
        entityColumn = "date"
    )
    val standchen: Standchen
)

data class StandchenWithJubilare (
    @Embedded val standchen: Standchen,
    @Relation(
        parentColumn = "date",
        entityColumn = "jubilarId",
        associateBy = Junction(StandchenInvite::class)
    )
    val jubilare: List<Jubilar>
)