package dev.tekofx.biblioteques.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val SHOW_TUTORIAL = booleanPreferencesKey("show_tutorial")
    }

    val currentShowTutorial: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[SHOW_TUTORIAL] ?: true
        }

    suspend fun saveShowTutorial(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_TUTORIAL] = value
        }
    }
}