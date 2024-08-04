package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.Standchen

@Dao
interface StandchenDao {
    @Query("SELECT * FROM Standchen WHERE strftime('%Y', date) = CAST(:year AS TEXT)")
    fun getStandchen(year: Int): Flow<List<Standchen>>

    @Insert
    suspend fun insert(standchen: List<Standchen>)
}
