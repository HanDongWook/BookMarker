package com.hdw.bookmarker.core.datastore.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hdw.bookmarker.core.datastore.base.BasePreferencesDatastore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val DATASTORE_NAME = "bookmarker_datastore"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

@Singleton
class BookMarkerDatastore @Inject constructor(@ApplicationContext context: Context) :
    BasePreferencesDatastore(context.dataStore) {

    object Keys {
        val defaultBrowserPackage = stringPreferencesKey("default_browser_package")
    }

    suspend fun saveDefaultBrowserPackage(value: String) {
        saveString(Keys.defaultBrowserPackage, value)
    }

    fun getDefaultBrowserPackage(): Flow<String?> = safeData.map { preferences ->
        preferences[Keys.defaultBrowserPackage]
    }
}
