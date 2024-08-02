package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import model.Jubilar

@Database(entities = [Jubilar::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun jubilarDao(): JubilarDao
}

internal const val dbFileName = "Jubilare.db"