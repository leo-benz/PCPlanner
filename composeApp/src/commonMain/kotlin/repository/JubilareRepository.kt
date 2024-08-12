package repository

import database.AppDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import model.Jubilar
import model.JubilarWithInvites
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface JubilareRepository {
    fun greet(): String
    fun insert(jubilar: Jubilar)
    fun getJubilare(): Flow<List<Jubilar>>
    fun getJubilare(date: LocalDate): Flow<List<Jubilar>>
    fun deletaAll()
    fun getJubilareWithInvites(day: LocalDate): Flow<List<JubilarWithInvites>>
}

class JubilareRepositoryImpl : JubilareRepository, KoinComponent {
    override fun greet(): String {
        return "Hello from JubilareRepository"
    }

    override fun insert(jubilar: Jubilar) {
        coroutineScope.launch {
            database.jubilarDao().insert(jubilar)
        }
    }

    override fun getJubilare(): Flow<List<Jubilar>> {
        return database.jubilarDao().getAllAsFlow()
    }

    override fun getJubilare(date: LocalDate): Flow<List<Jubilar>> {
        return database.jubilarDao().getJubilare(date)
    }

    override fun deletaAll() {
        coroutineScope.launch {
            database.jubilarDao().deleteAll()
        }
    }

    override fun getJubilareWithInvites(day: LocalDate): Flow<List<JubilarWithInvites>> {
        return database.jubilarDao().getJubilareWithInvites(day)
    }

    private val database: AppDatabase by inject()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
}