package dev.tekofx.bibliotecat.ui.viewModels.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.core.content.edit


class Preferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)


    fun isDynamicColorEnabled(): Boolean {
        return sharedPreferences.getBoolean("dynamic_color", false)
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean("dynamic_color", enabled) }
    }

    fun isShowWelcomeScreen(): Boolean {
        return sharedPreferences.getBoolean("show_welcome_screen", true)
    }

    fun setShowWelcomeScreen(enabled: Boolean) {
        sharedPreferences.edit { putBoolean("show_welcome_screen", enabled) }
    }
}

class PreferencesViewModel(private val preferences: Preferences) : ViewModel() {
    private val _isDynamicColorEnabled = MutableStateFlow(preferences.isDynamicColorEnabled())
    val isDynamicColorEnabled: StateFlow<Boolean> = _isDynamicColorEnabled
    private val _isShowWelcomeScreen = MutableStateFlow(preferences.isShowWelcomeScreen())
    val isShowWelcomeScreen: StateFlow<Boolean> = _isShowWelcomeScreen

    fun setDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setDynamicColorEnabled(enabled)
            _isDynamicColorEnabled.value = enabled
        }
    }

    fun setShowWelcomeScreen(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setShowWelcomeScreen(enabled)
            _isDynamicColorEnabled.value = enabled
        }
    }
}