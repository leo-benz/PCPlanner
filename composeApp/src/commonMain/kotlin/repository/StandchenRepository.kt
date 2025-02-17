@file:OptIn(ExperimentalUuidApi::class)

package repository

import database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import model.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.openapitools.client.apis.HolidaysApi
import java.io.File
import kotlin.uuid.ExperimentalUuidApi

interface StandchenRepository {
    fun getStandchen(year: Int): Flow<List<Standchen>>
    fun getStandchen(date: LocalDate): Flow<Standchen?>
    fun getStandchenWithJubilare(date: LocalDate): Flow<StandchenWithJubilare?>
    suspend fun insert(standchen: List<Standchen>)
    suspend fun insert(standchen: Standchen)
    suspend fun remove(standchen: Standchen)
    fun getSummerHoliday(year: Int): Flow<Holiday?>
    fun insert(invite: StandchenInvite)
    fun getStandchen(jubilar: Jubilar, year: Int): Flow<Standchen>
    fun insert(holiday: Holiday)
    fun getStandchenWithJubilare(year: Int): Flow<List<StandchenWithJubilare>>
    suspend fun isFirstAfterHoliday(standchen: Standchen, holiday: Holiday, logFile: File): Boolean
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

    @OptIn(ExperimentalUuidApi::class)
    override fun getStandchen(jubilar: Jubilar, year: Int): Flow<Standchen> {
        return database.standchenDao().getStandchen(jubilar.jubilarId, year)
    }

    override fun getStandchenWithJubilare(year: Int): Flow<List<StandchenWithJubilare>> {
        return database.standchenDao().getStandchenWithJubilare(year).map { it.map { it.toDomain() } }
    }

    override fun getStandchenWithJubilare(date: LocalDate): Flow<StandchenWithJubilare?> {
        return database.standchenDao().getStandchenWithJubilare(date).map { it?.toDomain() }
    }

    override suspend fun insert(standchen: List<Standchen>) {
        database.standchenDao().insert(standchen)
    }

    override suspend fun insert(standchen: Standchen) {
        database.standchenDao().insert(standchen)
    }

    override fun insert(invite: StandchenInvite) {
        coroutineScope.launch {
            database.standchenDao().insert(invite)
        }
    }

    override fun insert(holiday: Holiday) {
        coroutineScope.launch {
            database.standchenDao().insert(holiday)
        }
    }

    override suspend fun remove(standchen: Standchen) {
        database.standchenDao().delete(standchen)
    }

    override suspend fun isFirstAfterHoliday(standchen: Standchen, holiday: Holiday, logFile: File): Boolean {
        try {
            // Fetch all Standchen after the holiday
            logFile.appendText("Fetching Standchen after holiday ${holiday.startDate} for comparison\n")
            val standchenFlowAfterHoliday = database.standchenDao().getStandchenAfterDate(holiday.endDate)
            val standchenAfterHoliday = standchenFlowAfterHoliday.first()

            // Sort by date to ensure chronological order
            logFile.appendText("Sorting Standchen after holiday\n")
            val sorted = standchenAfterHoliday.sortedBy { it.date }

            logFile.appendText("Checking if Standchen is first after holiday\n")
            // Check if the given Standchen is the first in the sorted list
            return sorted.firstOrNull()?.date == standchen.date
        } catch (e: Exception) {
            logFile.appendText("Error fetching Standchen after holiday: ${e.message}\n")
            e.printStackTrace()
            return false
        }
    }

    @Throws(SummerHolidayFetchException::class)
    override fun getSummerHoliday(year: Int): Flow<Holiday?> = flow {
        val holidayFlow = database.standchenDao().getSummerHoliday(year).onStart { println("Repo Flow started for year = $year") }
            .onEach { holiday -> println("Repo Flow emitted holiday = $holiday") }
            .onCompletion { cause -> println("Repo Flow completed. Cause: $cause") }

        val storedHoliday = database.standchenDao().getSummerHoliday(year).firstOrNull()

        if (storedHoliday == null) {
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
            try {
                val holidayResponse = holidays.body().first() { response ->
                    response.name.map { it.text }.contains("Sommerferien")
                }
                database.standchenDao().insert(Holiday(holidayResponse.startDate, holidayResponse.endDate))
            } catch (e: NoSuchElementException) {
                println("No summer holiday found for $year no such element!!!")
//                throw SummerHolidayNotFoundException("No summer holiday found for $year", e)
            } catch (e: Exception) {
                println("Generic Exception in holiday fetching: ${e.message}")
                e.printStackTrace()
//                throw SummerHolidayFetchException("Error decoding holiday API response", e)
            }
        }

        emitAll(holidayFlow)
    }
}

class SummerHolidayFetchException(message: String, cause: Throwable? = null) : Exception(message, cause)
class SummerHolidayNotFoundException(message: String, cause: Throwable? = null) : Exception(message, cause)


