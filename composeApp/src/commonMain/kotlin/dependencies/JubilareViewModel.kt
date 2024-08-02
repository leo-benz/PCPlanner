package dependencies

import androidx.lifecycle.ViewModel

class JubilareViewModel(
    private val repository: JubilareRepository
): ViewModel() {
    fun greet(): String {
        return repository.greet()
    }
}