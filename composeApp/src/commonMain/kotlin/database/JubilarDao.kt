@file:OptIn(ExperimentalUuidApi::class)

package database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import model.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Dao
interface JubilarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jubilar: JubilarEntity)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(jubilar: AnniversaryJubilar)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(jubilar: BirthdayJubilar)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(jubilars: List<AnniversaryJubilar>)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(jubilars: List<BirthdayJubilar>)

    @Query("SELECT * FROM JubilarEntity ORDER BY originalJubilarDate")
    fun getAllJubilare(): Flow<List<JubilarEntity>>

//    @Query("SELECT * FROM BirthdayJubilar")
//    fun getBirthdayAsFlow(): Flow<List<BirthdayJubilar>>
//
//    @Query("SELECT * FROM AnniversaryJubilar")
//    fun getAnniversaryAsFlow(): Flow<List<AnniversaryJubilar>>

//    @Query("SELECT COUNT(*) as count FROM Jubilar")
//    suspend fun getCount(): Int

    @Query("DELETE FROM JubilarEntity")
    suspend fun deleteAll()

//    @Query("DELETE FROM BirthdayJubilar")
//    suspend fun deleteBirthday()
//
//    @Query("DELETE FROM AnniversaryJubilar")
//    suspend fun deleteAnniversary()

    @Delete
    suspend fun delete(jubilar: JubilarEntity)

//    @Delete
//    suspend fun delete(jubilar: BirthdayJubilar)
//
//    @Delete
//    suspend fun delete(jubilar: AnniversaryJubilar)

    @Transaction
    @Query("SELECT * FROM JubilarEntity WHERE strftime('%m-%d', originalJubilarDate) = strftime('%m-%d', :date) ORDER BY originalJubilarDate")
    fun getJubilare(date: LocalDate): Flow<List<JubilarEntity>>

//    @Transaction
//    @Query("SELECT * FROM BirthdayJubilar WHERE strftime('%m-%d', originalJubilarDate) = strftime('%m-%d', :date)")
//    fun getBirthdayJubilare(date: LocalDate): Flow<List<BirthdayJubilar>>
//
//    @Transaction
//    @Query("SELECT * FROM AnniversaryJubilar WHERE strftime('%m-%d', originalJubilarDate) = strftime('%m-%d', :date)")
//    fun getAnniversaryJubilare(date: LocalDate): Flow<List<AnniversaryJubilar>>

//    @Transaction
//    @Query("SELECT * FROM Jubilar WHERE strftime('%m-%d', originalJubilarDate) = strftime('%m-%d', :date)")
//    fun getJubilareWithInvites(date: LocalDate): Flow<List<JubilarWithInvites>>

    @Query("SELECT EXISTS(SELECT 1 FROM JubilarEntity WHERE jubilarId = :uuid)")
    fun exists(uuid: Uuid): Flow<Boolean>

    @Query("SELECT * FROM JubilarEntity WHERE lastName = :lastName AND originalJubilarDate = :originalJubilarDate AND firstName = :firstName AND gender = :gender AND type = :type LIMIT 1")
    fun getJubilarByData(
        lastName: String,
        originalJubilarDate: LocalDate,
        firstName: String?,
        gender: Gender?,
        type: JubilarType
    ): Flow<JubilarEntity?>

    @Query("""
    SELECT * FROM JubilarEntity
    WHERE 
        (firstName || ' ' || lastName LIKE '%' || :query || '%' 
        OR lastName || ' ' || firstName LIKE '%' || :query || '%'
        OR address LIKE '%' || :query || '%')
    ORDER BY originalJubilarDate
""")
    fun filterJubilare(query: String): Flow<List<JubilarEntity>>
}
