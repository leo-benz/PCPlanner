package di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import database.AppDatabase
import database.dbFileName
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File

actual val platformModule = module {
    single<AppDatabase> { createRoomDatabase() }
}

fun createRoomDatabase(): AppDatabase {
    val dbFile = File(System.getProperty("java.io.tmpdir"), dbFileName)
    return Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .build()
}