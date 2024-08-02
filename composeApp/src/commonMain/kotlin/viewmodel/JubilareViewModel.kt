package viewmodel

import androidx.lifecycle.ViewModel
import repository.JubilareRepository

class JubilareViewModel(
    private val repository: JubilareRepository
): ViewModel() {
    fun greet(): String {
        return repository.greet()
    }
}