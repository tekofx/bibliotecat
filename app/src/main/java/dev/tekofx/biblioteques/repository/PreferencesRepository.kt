package dev.tekofx.biblioteques.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val SHOW_WELCOME_SCREEN = booleanPreferencesKey("show_welcome_screen")
    }

    val currentShowTutorial: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[SHOW_WELCOME_SCREEN] ?: true
        }

    suspend fun saveShowWelcomeScreen(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_WELCOME_SCREEN] = value
        }
    }
}