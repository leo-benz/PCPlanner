package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import model.Jubilar

@Database(entities = [Jubilar::class], version = 1)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase: RoomDatabase(), DB {
    abstract fun jubilarDao(): JubilarDao

    override fun clearAllTables() {
        super.clearAllTables()
    }
}

internal const val dbFileName = "Jubilare.db"

interface DB {
    fun clearAllTables(): Unit {}
}