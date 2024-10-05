package repository

import database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import model.Standchen
import model.StandchenInvite
import model.StandchenWithJubilare
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import org.openapitools.client.apis.HolidaysApi
import org.openapitools.client.models.HolidayResponse
import org.openapitools.client.models.HolidayType

interface StandchenRepository {
    fun getStandchen(year: Int): Flow<List<Standchen>>
    fun getStandchen(date: LocalDate): Flow<Standchen?>
    fun getStandchenWithJubilare(date: LocalDate): Flow<StandchenWithJubilare?>
    fun insert(standchen: List<Standchen>)
    suspend fun insert(standchen: Standchen)
    suspend fun remove(standchen: Standchen)
    suspend fun getSummerHoliday(year: Int): HolidayResponse?
    fun insert(invite: StandchenInvite)
}

class StandchenRepositoryImpl : StandchenRepository, KoinComponent {
    private val database: AppDatabase by inject()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun getStandchen(year: Int): Flow<List<Standchen>> {
        return database.standchenDao().getStandchen(year)
    }

    override fun getStandchen(date: LocalDate): Flow<Standchen?> {
        return database.standchenDao().getSingleStandchen(date)
    }

    override fun getStandchenWithJubilare(date: LocalDate): Flow<StandchenWithJubilare?> {
        return database.standchenDao().getStandchenWithJubilare(date)
    }

    override fun insert(standchen: List<Standchen>) {
        coroutineScope.launch {
            database.standchenDao().insert(standchen)
        }
    }

    override suspend fun insert(standchen: Standchen) {
        database.standchenDao().insert(standchen)
    }

    override fun insert(invite: StandchenInvite) {
        coroutineScope.launch {
            database.standchenDao().insert(invite)
        }
    }

    override suspend fun remove(standchen: Standchen) {
        database.standchenDao().delete(standchen)
    }

    override suspend fun getSummerHoliday(year: Int): HolidayResponse? {
        val holidaysApi = HolidaysApi("https://openholidaysapi.org/")

        val holidays = withContext(Dispatchers.IO) {
            holidaysApi.schoolHolidaysGet(
                "DE",
                LocalDate(year, 1, 1),
                LocalDate(year, 12, 31),
                "DE",
                "DE-BW"
            )
        }
        return holidays.body().firstOrNull {
            it.name.map { it.text }.contains("Sommerferien")
        }
    }
}


