package database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import model.Standchen
import model.StandchenInvite
import model.StandchenWithJubilare

@Dao
interface StandchenDao {
    @Query("SELECT * FROM Standchen WHERE strftime('%Y', date) = CAST(:year AS TEXT) ORDER BY date")
    fun getStandchen(year: Int): Flow<List<Standchen>>

    @Insert
    suspend fun insert(standchen: List<Standchen>)

    @Insert
    suspend fun insert(standchen: Standchen)

    @Delete
    suspend fun delete(standchen: Standchen)

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(invite: StandchenInvite)
}
