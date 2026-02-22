package com.hdw.bookmarker.core.ui.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.settingsDataStore by preferencesDataStore(name = "settings_preferences")

private object SettingsPreferenceKeys {
    val DefaultBrowserPackage = stringPreferencesKey("default_browser_package")
}

fun Context.getDefaultBrowserPackageFlow(): Flow<String?> = settingsDataStore.data
    .catch { throwable ->
        if (throwable is IOException) emit(emptyPreferences()) else throw throwable
    }
    .map { preferences -> preferences[SettingsPreferenceKeys.DefaultBrowserPackage] }

suspend fun Context.setDefaultBrowserPackage(packageName: String) {
    settingsDataStore.edit { preferences ->
        preferences[SettingsPreferenceKeys.DefaultBrowserPackage] = packageName
    }
}
