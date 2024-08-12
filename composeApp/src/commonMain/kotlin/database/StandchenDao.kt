package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import model.Standchen
import model.StandchenInvite
import model.StandchenWithJubilare

@Dao
interface StandchenDao {
    @Query("SELECT * FROM Standchen WHERE strftime('%Y', date) = CAST(:year AS TEXT)")
    fun getStandchen(year: Int): Flow<List<Standchen>>

    @Insert
    suspend fun insert(standchen: List<Standchen>)

//    @Transaction
//    @Query("SELECT * FROM Standchen WHERE strftime('%Y', date) = CAST(:year AS TEXT)")
//    fun getStandchenWithJubilare(year: Int): Flow<List<StandchenWithJubilare>>
//
    @Transaction
    @Query("SELECT * FROM Standchen WHERE date = :date")
    fun getStandchenWithJubilare(date: LocalDate): Flow<StandchenWithJubilare?>

    @Transaction
    @Query("SELECT * FROM Standchen WHERE date = :date")
    fun getSingleStandchen(date: LocalDate): Flow<Standchen?>

    @Insert
    suspend fun insert(invite: StandchenInvite)
}
