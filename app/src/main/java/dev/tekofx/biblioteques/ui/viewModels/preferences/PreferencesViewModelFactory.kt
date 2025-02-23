package dev.tekofx.biblioteques.ui.viewModels.preferences

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Singleton

@Singleton
@Suppress("UNCHECKED_CAST")
class PreferencesViewModelFactory(private val preferences: Preferences) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        Log.d("PreferencesViewModelFactory", "create called")
        return if (modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
            PreferencesViewModel(this.preferences) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}