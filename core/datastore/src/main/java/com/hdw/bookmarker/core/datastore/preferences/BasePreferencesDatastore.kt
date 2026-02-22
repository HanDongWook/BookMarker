package com.hdw.bookmarker.core.datastore.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

abstract class BasePreferencesDatastore(protected val dataStore: DataStore<Preferences>) {
    protected val safeData: Flow<Preferences> =
        dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    protected suspend fun saveString(key: Preferences.Key<String>, value: String) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    protected fun getString(key: Preferences.Key<String>, defaultValue: String = ""): Flow<String> =
        safeData.map { preferences ->
            preferences[key] ?: defaultValue
        }
}
