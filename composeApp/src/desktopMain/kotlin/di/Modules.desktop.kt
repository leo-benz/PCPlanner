package di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import database.AppDatabase
import database.dbFileName
import org.koin.dsl.module
import java.io.File

actual val platformModule = module {
    single<AppDatabase> { createRoomDatabase() }
}

fun createRoomDatabase(): AppDatabase {
    val dbFile = File(System.getProperty("user.home"), dbFileName)
    return Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(true)
        .build()
}