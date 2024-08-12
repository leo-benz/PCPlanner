package database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import model.Jubilar
import model.JubilarWithInvites

@Dao
interface JubilarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jubilar: Jubilar)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(jubilars: List<Jubilar>)

    @Query("SELECT * FROM Jubilar")
    fun getAllAsFlow(): Flow<List<Jubilar>>

    @Query("SELECT COUNT(*) as count FROM Jubilar")
    suspend fun getCount(): Int

    @Query("DELETE FROM Jubilar")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(jubilar: Jubilar)

    @Transaction
    @Query("SELECT * FROM Jubilar WHERE strftime('%m-%d', birthdate) = strftime('%m-%d', :date)")
    fun getJubilare(date: LocalDate): Flow<List<Jubilar>>

    @Transaction
    @Query("SELECT * FROM Jubilar WHERE strftime('%m-%d', birthdate) = strftime('%m-%d', :date)")
    fun getJubilareWithInvites(date: LocalDate): Flow<List<JubilarWithInvites>>
}
