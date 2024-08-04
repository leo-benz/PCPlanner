package model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity
data class Standchen (
    @PrimaryKey val date: LocalDate,
    val summerBreak: Boolean
)