package com.hdw.bookmarker.core.data.repository

import com.hdw.bookmarker.core.datastore.preferences.BookMarkerDatastore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(private val bookMarkerDatastore: BookMarkerDatastore) :
    SettingsRepository {

    override fun getDefaultBrowserPackageFlow(): Flow<String?> = bookMarkerDatastore.getDefaultBrowserPackage()
    override fun getBookmarkDisplayTypeFlow(): Flow<String?> = bookMarkerDatastore.getBookmarkDisplayType()
    override fun getBookmarkSecondaryDisplayTypeFlow(): Flow<String?> = bookMarkerDatastore.getBookmarkSecondaryDisplayType()
    override fun getScrollLongBookmarkUrlFlow(): Flow<Boolean> = bookMarkerDatastore.getScrollLongBookmarkUrl()
    override fun getOpenBookmarkAdjacentOnLargeScreenFlow(): Flow<Boolean> =
        bookMarkerDatastore.getOpenBookmarkAdjacentOnLargeScreen()
    override fun getOpenBookmarkSidePreviewOnLargeScreenFlow(): Flow<Boolean> =
        bookMarkerDatastore.getOpenBookmarkSidePreviewOnLargeScreen()
    override fun getShowFolderDescriptionFlow(): Flow<Boolean> = bookMarkerDatastore.getShowFolderDescription()
    override fun getScrollLongFolderDescriptionFlow(): Flow<Boolean> =
        bookMarkerDatastore.getScrollLongFolderDescription()
    override fun getAppThemeModeFlow(): Flow<String?> = bookMarkerDatastore.getAppThemeMode()
    override fun getBookmarkFolderIconShapeFlow(): Flow<String?> = bookMarkerDatastore.getBookmarkFolderIconShape()
    override fun getBookmarkFolderIconColorFlow(): Flow<String?> = bookMarkerDatastore.getBookmarkFolderIconColor()

    override suspend fun setDefaultBrowserPackage(packageName: String) {
        bookMarkerDatastore.saveDefaultBrowserPackage(packageName)
    }

    override suspend fun setBookmarkDisplayType(displayType: String) {
        bookMarkerDatastore.saveBookmarkDisplayType(displayType)
    }

    override suspend fun setBookmarkSecondaryDisplayType(displayType: String) {
        bookMarkerDatastore.saveBookmarkSecondaryDisplayType(displayType)
    }

    override suspend fun setScrollLongBookmarkUrl(enabled: Boolean) {
        bookMarkerDatastore.saveScrollLongBookmarkUrl(enabled)
    }

    override suspend fun setOpenBookmarkAdjacentOnLargeScreen(enabled: Boolean) {
        bookMarkerDatastore.saveOpenBookmarkAdjacentOnLargeScreen(enabled)
    }

    override suspend fun setOpenBookmarkSidePreviewOnLargeScreen(enabled: Boolean) {
        bookMarkerDatastore.saveOpenBookmarkSidePreviewOnLargeScreen(enabled)
    }

    override suspend fun setShowFolderDescription(show: Boolean) {
        bookMarkerDatastore.saveShowFolderDescription(show)
    }

    override suspend fun setScrollLongFolderDescription(enabled: Boolean) {
        bookMarkerDatastore.saveScrollLongFolderDescription(enabled)
    }

    override suspend fun setAppThemeMode(mode: String) {
        bookMarkerDatastore.saveAppThemeMode(mode)
    }

    override suspend fun setBookmarkFolderIconShape(shape: String) {
        bookMarkerDatastore.saveBookmarkFolderIconShape(shape)
    }

    override suspend fun setBookmarkFolderIconColor(color: String) {
        bookMarkerDatastore.saveBookmarkFolderIconColor(color)
    }
}
