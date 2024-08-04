package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import model.Jubilar
import model.Standchen

@Database(entities = [Jubilar::class, Standchen::class], version = 2)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase: RoomDatabase(), DB {
    abstract fun jubilarDao(): JubilarDao
    abstract fun standchenDao(): StandchenDao

    override fun clearAllTables() {
        super.clearAllTables()
    }

}

internal const val dbFileName = "Jubilare.db"

interface DB {
    fun clearAllTables(): Unit {}
}