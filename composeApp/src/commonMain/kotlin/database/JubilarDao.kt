package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.Jubilar

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
}
