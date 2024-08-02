package repository

import database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface JubilareRepository {
    fun greet(): String
}

class JubilareRepositoryImpl: JubilareRepository, KoinComponent {
    override fun greet(): String {
        return "Hello from JubilareRepository"
    }

    private val database: AppDatabase by inject()

    val coroutineScope = CoroutineScope(Dispatchers.Default)

}