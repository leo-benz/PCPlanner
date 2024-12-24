package model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity
data class Holiday(
    val startDate: LocalDate,
    val endDate: LocalDate,
    @PrimaryKey val year: Int = startDate.year
)
