package dev.tekofx.biblioteques.ui.viewModels.preferences

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.tekofx.biblioteques.repository.PreferencesRepository
import javax.inject.Singleton

@Singleton
@Suppress("UNCHECKED_CAST")
class PreferencesViewModelFactory(private val repository: PreferencesRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        Log.d("PreferencesViewModelFactory", "create called")
        return if (modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
            PreferencesViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}