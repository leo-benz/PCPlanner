package repository

import database.AppDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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
//    fun getJubilareWithInvites(day: LocalDate): Flow<List<JubilarWithInvites>>
}

@OptIn(ExperimentalCoroutinesApi::class)
class JubilareRepositoryImpl : JubilareRepository, KoinComponent {
    override fun greet(): String {
        return "Hello from JubilareRepository"
    }

    override fun insert(jubilar: Jubilar) {
        coroutineScope.launch {
            if (jubilar is model.AnniversaryJubilar) {
                database.jubilarDao().insert(jubilar)
            } else if (jubilar is model.BirthdayJubilar) {
                database.jubilarDao().insert(jubilar)
            }
        }
    }

    override fun getJubilare(): Flow<List<Jubilar>> {
        return combine(
            database.jubilarDao().getBirthdayAsFlow(),
            database.jubilarDao().getAnniversaryAsFlow()
        ) { birthday, anniversary ->
            birthday + anniversary
        }
    }

    override fun getJubilare(date: LocalDate): Flow<List<Jubilar>> {
        return combine(
            database.jubilarDao().getBirthdayJubilare(date),
            database.jubilarDao().getAnniversaryJubilare(date)
        ) { birthday, anniversary ->
            birthday + anniversary
        }
    }

    override fun deletaAll() {
        coroutineScope.launch {
            database.jubilarDao().deleteBirthday()
            database.jubilarDao().deleteAnniversary()
        }
    }

//    override fun getJubilareWithInvites(day: LocalDate): Flow<List<JubilarWithInvites>> {
//        return database.jubilarDao().getJubilareWithInvites(day)
//    }

    private val database: AppDatabase by inject()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
}