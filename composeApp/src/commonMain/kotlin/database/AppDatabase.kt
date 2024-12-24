package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import model.*

@Database(entities = [Jubilar::class, AnniversaryJubilar::class, BirthdayJubilar::class, Standchen::class, StandchenInvite::class, Holiday::class], version = 16)
@TypeConverters(LocalDateConverter::class, UuidConverter::class)
abstract class AppDatabase: RoomDatabase(), DB {
    abstract fun jubilarDao(): JubilarDao
    abstract fun standchenDao(): StandchenDao

}

internal const val dbFileName = "Jubilare.db"

interface DB {
    fun clearAllTables() {}
}