package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import model.Jubilar
import model.Standchen
import model.StandchenInvite

@Database(entities = [Jubilar::class, Standchen::class, StandchenInvite::class], version = 12)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase: RoomDatabase(), DB {
    abstract fun jubilarDao(): JubilarDao
    abstract fun standchenDao(): StandchenDao

}

internal const val dbFileName = "Jubilare.db"

interface DB {
    fun clearAllTables() {}
}