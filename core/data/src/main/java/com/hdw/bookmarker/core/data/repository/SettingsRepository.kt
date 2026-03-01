package com.hdw.bookmarker.core.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getDefaultBrowserPackageFlow(): Flow<String?>
    fun getBookmarkDisplayTypeFlow(): Flow<String?>
    fun getAppThemeModeFlow(): Flow<String?>

    suspend fun setDefaultBrowserPackage(packageName: String)
    suspend fun setBookmarkDisplayType(displayType: String)
    suspend fun setAppThemeMode(mode: String)

    companion object {
        const val APP_THEME_MODE_LIGHT = "LIGHT"
        const val APP_THEME_MODE_DARK = "DARK"
    }
}
