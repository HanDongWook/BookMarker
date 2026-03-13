package com.hdw.bookmarker.core.datastore.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
        val bookmarkDisplayType = stringPreferencesKey("bookmark_display_type")
        val showBookmarkUrl = booleanPreferencesKey("show_bookmark_url")
        val scrollLongBookmarkUrl = booleanPreferencesKey("scroll_long_bookmark_url")
        val openBookmarkAdjacentOnLargeScreen = booleanPreferencesKey("open_bookmark_adjacent_on_large_screen")
        val openBookmarkSidePreviewOnLargeScreen =
            booleanPreferencesKey("open_bookmark_side_preview_on_large_screen")
        val showFolderDescription = booleanPreferencesKey("show_folder_description")
        val scrollLongFolderDescription = booleanPreferencesKey("scroll_long_folder_description")
        val appThemeMode = stringPreferencesKey("app_theme_mode")
        val bookmarkFolderIconShape = stringPreferencesKey("bookmark_folder_icon_shape")
        val bookmarkFolderIconColor = stringPreferencesKey("bookmark_folder_icon_color")
    }

    suspend fun saveDefaultBrowserPackage(value: String) {
        saveString(Keys.defaultBrowserPackage, value)
    }

    fun getDefaultBrowserPackage(): Flow<String?> = safeData.map { preferences ->
        preferences[Keys.defaultBrowserPackage]
    }

    suspend fun saveBookmarkDisplayType(value: String) {
        saveString(Keys.bookmarkDisplayType, value)
    }

    fun getBookmarkDisplayType(): Flow<String?> = safeData.map { preferences ->
        preferences[Keys.bookmarkDisplayType]
    }

    suspend fun saveShowBookmarkUrl(value: Boolean) {
        saveBoolean(Keys.showBookmarkUrl, value)
    }

    fun getShowBookmarkUrl(): Flow<Boolean> = getBoolean(
        key = Keys.showBookmarkUrl,
        defaultValue = true,
    )

    suspend fun saveScrollLongBookmarkUrl(value: Boolean) {
        saveBoolean(Keys.scrollLongBookmarkUrl, value)
    }

    fun getScrollLongBookmarkUrl(): Flow<Boolean> = getBoolean(
        key = Keys.scrollLongBookmarkUrl,
        defaultValue = true,
    )

    suspend fun saveOpenBookmarkAdjacentOnLargeScreen(value: Boolean) {
        saveBoolean(Keys.openBookmarkAdjacentOnLargeScreen, value)
    }

    fun getOpenBookmarkAdjacentOnLargeScreen(): Flow<Boolean> = getBoolean(
        key = Keys.openBookmarkAdjacentOnLargeScreen,
        defaultValue = false,
    )

    suspend fun saveOpenBookmarkSidePreviewOnLargeScreen(value: Boolean) {
        saveBoolean(Keys.openBookmarkSidePreviewOnLargeScreen, value)
    }

    fun getOpenBookmarkSidePreviewOnLargeScreen(): Flow<Boolean> = getBoolean(
        key = Keys.openBookmarkSidePreviewOnLargeScreen,
        defaultValue = false,
    )

    suspend fun saveShowFolderDescription(value: Boolean) {
        saveBoolean(Keys.showFolderDescription, value)
    }

    fun getShowFolderDescription(): Flow<Boolean> = getBoolean(
        key = Keys.showFolderDescription,
        defaultValue = true,
    )

    suspend fun saveScrollLongFolderDescription(value: Boolean) {
        saveBoolean(Keys.scrollLongFolderDescription, value)
    }

    fun getScrollLongFolderDescription(): Flow<Boolean> = getBoolean(
        key = Keys.scrollLongFolderDescription,
        defaultValue = true,
    )

    suspend fun saveAppThemeMode(value: String) {
        saveString(Keys.appThemeMode, value)
    }

    fun getAppThemeMode(): Flow<String?> = safeData.map { preferences ->
        preferences[Keys.appThemeMode]
    }

    suspend fun saveBookmarkFolderIconShape(value: String) {
        saveString(Keys.bookmarkFolderIconShape, value)
    }

    fun getBookmarkFolderIconShape(): Flow<String?> = safeData.map { preferences ->
        preferences[Keys.bookmarkFolderIconShape]
    }

    suspend fun saveBookmarkFolderIconColor(value: String) {
        saveString(Keys.bookmarkFolderIconColor, value)
    }

    fun getBookmarkFolderIconColor(): Flow<String?> = safeData.map { preferences ->
        preferences[Keys.bookmarkFolderIconColor]
    }
}
