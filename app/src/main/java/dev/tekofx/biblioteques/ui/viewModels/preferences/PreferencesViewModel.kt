package dev.tekofx.biblioteques.ui.viewModels.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tekofx.biblioteques.repository.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiState(
    val showWelcomeScreen: Boolean
)

class PreferencesViewModel(private val repository: PreferencesRepository) : ViewModel() {
    val uiState: StateFlow<UiState> =
        repository.currentShowTutorial.map { showWelcomeScreen ->
            UiState(showWelcomeScreen)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState(false)
        )

    fun saveShowTutorial(value: Boolean) {
        viewModelScope.launch {
            repository.saveShowWelcomeScreen(value)
        }
    }
}