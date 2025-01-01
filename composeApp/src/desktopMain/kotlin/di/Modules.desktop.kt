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

fun getAppDataDirectory(): File {
    val appName = "PCPlanner"
    val os = System.getProperty("os.name").lowercase()

    return when {
        "win" in os ->
            // %APPDATA%\AppName
            File(System.getenv("APPDATA") ?: System.getProperty("user.home"), appName)
        "mac" in os ->
            // ~/Library/Application Support/AppName
            File(System.getProperty("user.home"), "Library/Application Support/$appName")
        else ->
            // ~/.appname (or use XDG environment vars if you prefer)
            File(System.getProperty("user.home"), ".$appName")
    }
}

fun createRoomDatabase(): AppDatabase {
    // e.g. "PCPlanner" as your app name
    val appDataDir = getAppDataDirectory().apply { mkdirs() }

    // e.g. "mydatabase.db" as your dbFileName
    val dbFile = File(appDataDir, dbFileName)

    val dbBuilder =  Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())

//    dbBuilder.fallbackToDestructiveMigration(true)

    return dbBuilder.build()
}