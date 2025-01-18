@file:OptIn(ExperimentalUuidApi::class)

package repository

import database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import model.Jubilar
import model.toDomain
import model.toEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface JubilareRepository {
    fun greet(): String
    fun insert(jubilar: Jubilar)
    fun getJubilare(): Flow<List<Jubilar>>
    fun getJubilare(date: LocalDate): Flow<List<Jubilar>>
    fun deletaAll()
    fun exists(jubilarId: Uuid): Flow<Boolean>
    fun getStoredJubilar(it: Jubilar): Flow<Jubilar?>
    fun delete(deletedJubilar: Jubilar)
    fun filterJubilare(query: String): Flow<List<Jubilar>>
//    fun getJubilareWithInvites(day: LocalDate): Flow<List<JubilarWithInvites>>
}

@OptIn(ExperimentalCoroutinesApi::class)
class JubilareRepositoryImpl : JubilareRepository, KoinComponent {
    override fun greet(): String {
        return "Hello from JubilareRepository"
    }

    override fun insert(jubilar: Jubilar) {
        coroutineScope.launch {
            database.jubilarDao().insert(jubilar.toEntity())
        }
    }

    override fun getJubilare(): Flow<List<Jubilar>> {
        return database.jubilarDao()
            .getAllJubilare()               // Flow<List<JubilarEntity>>
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getJubilare(date: LocalDate): Flow<List<Jubilar>> {
        return database.jubilarDao()
            .getJubilare(date)               // Flow<List<JubilarEntity>>
            .map { list -> list.map { it.toDomain() } }
    }

    override fun deletaAll() {
        coroutineScope.launch {
            database.jubilarDao().deleteAll()
        }
    }

    @ExperimentalUuidApi
    override fun exists(jubilarId: Uuid): Flow<Boolean> {
        return database.jubilarDao().exists(jubilarId)
    }

    override fun getStoredJubilar(it: Jubilar): Flow<Jubilar?> {
        val entity = it.toEntity()
        return database.jubilarDao().getJubilarByData(entity.lastName, entity.originalJubilarDate, entity.firstName, entity.gender, entity.type).map { it?.toDomain() }
    }

    override fun delete(deletedJubilar: Jubilar) {
        coroutineScope.launch {
            database.jubilarDao().delete(deletedJubilar.toEntity())
        }
    }

    override fun filterJubilare(query: String): Flow<List<Jubilar>> {
        return database.jubilarDao().filterJubilare(query).map { list -> list.map { it.toDomain() } }
    }

//    override fun getJubilareWithInvites(day: LocalDate): Flow<List<JubilarWithInvites>> {
//        return database.jubilarDao().getJubilareWithInvites(day)
//    }

    private val database: AppDatabase by inject()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
}