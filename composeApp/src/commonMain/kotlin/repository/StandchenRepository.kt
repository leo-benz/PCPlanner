package repository

import database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import model.Standchen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import org.openapitools.client.apis.HolidaysApi
import org.openapitools.client.models.HolidayResponse
import org.openapitools.client.models.HolidayType

interface StandchenRepository {
    fun getStandchen(year: Int): Flow<List<Standchen>>
    fun insert(standchen: List<Standchen>)
    suspend fun getSummerHoliday(year: Int): HolidayResponse?
}

class StandchenRepositoryImpl : StandchenRepository, KoinComponent {
    private val database: AppDatabase by inject()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun getStandchen(year: Int): Flow<List<Standchen>> {
        return database.standchenDao().getStandchen(year)
    }

    override fun insert(standchen: List<Standchen>) {
        coroutineScope.launch {
            database.standchenDao().insert(standchen)
        }
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


