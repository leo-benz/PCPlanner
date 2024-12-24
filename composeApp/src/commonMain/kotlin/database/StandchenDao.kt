@file:OptIn(ExperimentalUuidApi::class)

package database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import model.Holiday
import model.Standchen
import model.StandchenInvite
import model.StandchenWithJubilareEntity
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
    fun getStandchenWithJubilare(date: LocalDate): Flow<StandchenWithJubilareEntity?>

    @Query("""SELECT s.* FROM Standchen s
            INNER JOIN StandchenInvite si ON s.date = si.date
            WHERE si.jubilarId = :jubilarId AND strftime('%Y', s.date) = CAST(:year AS TEXT)""")
    fun getStandchen(jubilarId: Uuid, year: Int): Flow<Standchen>

    @Transaction
    @Query("SELECT * FROM Standchen WHERE date = :date")
    fun getSingleStandchen(date: LocalDate): Flow<Standchen?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(invite: StandchenInvite)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(holiday: Holiday)

    @Query("SELECT * FROM Holiday WHERE year = :year LIMIT 1")
    fun getSummerHoliday(year: Int): Flow<Holiday?>
}
