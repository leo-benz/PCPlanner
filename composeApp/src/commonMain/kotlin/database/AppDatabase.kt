package database

import androidx.room.*
import model.*

@ConstructedBy(AppDataBaseCtor::class)
@Database(entities = [JubilarEntity::class, Standchen::class, StandchenInvite::class, Holiday::class], version = 19)
@TypeConverters(LocalDateConverter::class, UuidConverter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun jubilarDao(): JubilarDao
    abstract fun standchenDao(): StandchenDao
}

expect object AppDataBaseCtor : RoomDatabaseConstructor<AppDatabase>

internal const val dbFileName = "Jubilare.db"
