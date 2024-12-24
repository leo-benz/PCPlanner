package database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import model.AnniversaryJubilar
import model.BirthdayJubilar
import model.Jubilar
import model.JubilarWithInvites

@Dao
interface JubilarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jubilar: AnniversaryJubilar)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jubilar: BirthdayJubilar)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(jubilars: List<AnniversaryJubilar>)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(jubilars: List<BirthdayJubilar>)

    @Query("SELECT * FROM BirthdayJubilar")
    fun getBirthdayAsFlow(): Flow<List<BirthdayJubilar>>

    @Query("SELECT * FROM AnniversaryJubilar")
    fun getAnniversaryAsFlow(): Flow<List<AnniversaryJubilar>>

//    @Query("SELECT COUNT(*) as count FROM Jubilar")
//    suspend fun getCount(): Int

    @Query("DELETE FROM BirthdayJubilar")
    suspend fun deleteBirthday()

    @Query("DELETE FROM AnniversaryJubilar")
    suspend fun deleteAnniversary()

    @Delete
    suspend fun delete(jubilar: BirthdayJubilar)

    @Delete
    suspend fun delete(jubilar: AnniversaryJubilar)

    @Transaction
    @Query("SELECT * FROM BirthdayJubilar WHERE strftime('%m-%d', originalJubilarDate) = strftime('%m-%d', :date)")
    fun getBirthdayJubilare(date: LocalDate): Flow<List<BirthdayJubilar>>

    @Transaction
    @Query("SELECT * FROM AnniversaryJubilar WHERE strftime('%m-%d', originalJubilarDate) = strftime('%m-%d', :date)")
    fun getAnniversaryJubilare(date: LocalDate): Flow<List<AnniversaryJubilar>>

//    @Transaction
//    @Query("SELECT * FROM Jubilar WHERE strftime('%m-%d', originalJubilarDate) = strftime('%m-%d', :date)")
//    fun getJubilareWithInvites(date: LocalDate): Flow<List<JubilarWithInvites>>
}
