package com.hdw.bookmarker.core.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getDefaultBrowserPackageFlow(): Flow<String?>
    fun getBookmarkDisplayTypeFlow(): Flow<String?>
    fun getAppThemeModeFlow(): Flow<String?>
    fun getBookmarkFolderIconShapeFlow(): Flow<String?>
    fun getBookmarkFolderIconColorFlow(): Flow<String?>

    suspend fun setDefaultBrowserPackage(packageName: String)
    suspend fun setBookmarkDisplayType(displayType: String)
    suspend fun setAppThemeMode(mode: String)
    suspend fun setBookmarkFolderIconShape(shape: String)
    suspend fun setBookmarkFolderIconColor(color: String)

    companion object {
        const val APP_THEME_MODE_LIGHT = "LIGHT"
        const val APP_THEME_MODE_DARK = "DARK"
        const val BOOKMARK_DISPLAY_TYPE_LIST = "LIST"
        const val BOOKMARK_DISPLAY_TYPE_ICON = "ICON"
    }
}
