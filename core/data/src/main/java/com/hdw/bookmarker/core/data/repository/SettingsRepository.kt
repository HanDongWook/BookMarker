package com.hdw.bookmarker.core.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getDefaultBrowserPackageFlow(): Flow<String?>
    fun getBookmarkDisplayTypeFlow(): Flow<String?>
    fun getShowBookmarkUrlFlow(): Flow<Boolean>
    fun getScrollLongBookmarkUrlFlow(): Flow<Boolean>
    fun getOpenBookmarkAdjacentOnLargeScreenFlow(): Flow<Boolean>
    fun getOpenBookmarkSidePreviewOnLargeScreenFlow(): Flow<Boolean>
    fun getShowFolderDescriptionFlow(): Flow<Boolean>
    fun getScrollLongFolderDescriptionFlow(): Flow<Boolean>
    fun getAppThemeModeFlow(): Flow<String?>
    fun getBookmarkFolderIconShapeFlow(): Flow<String?>
    fun getBookmarkFolderIconColorFlow(): Flow<String?>

    suspend fun setDefaultBrowserPackage(packageName: String)
    suspend fun setBookmarkDisplayType(displayType: String)
    suspend fun setShowBookmarkUrl(show: Boolean)
    suspend fun setScrollLongBookmarkUrl(enabled: Boolean)
    suspend fun setOpenBookmarkAdjacentOnLargeScreen(enabled: Boolean)
    suspend fun setOpenBookmarkSidePreviewOnLargeScreen(enabled: Boolean)
    suspend fun setShowFolderDescription(show: Boolean)
    suspend fun setScrollLongFolderDescription(enabled: Boolean)
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
