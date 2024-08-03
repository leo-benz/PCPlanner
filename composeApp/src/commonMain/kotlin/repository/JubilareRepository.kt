package repository

import database.AppDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import model.Jubilar
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface JubilareRepository {
    fun greet(): String
    fun insert(jubilar: Jubilar)
    fun getJubilare(): Flow<List<Jubilar>>
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

    private val database: AppDatabase by inject()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
}