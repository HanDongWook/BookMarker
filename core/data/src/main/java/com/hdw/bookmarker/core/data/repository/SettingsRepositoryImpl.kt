package com.hdw.bookmarker.core.data.repository

import com.hdw.bookmarker.core.datastore.preferences.BookMarkerDatastore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(private val bookMarkerDatastore: BookMarkerDatastore) :
    SettingsRepository {

    override fun getDefaultBrowserPackageFlow(): Flow<String?> = bookMarkerDatastore.getDefaultBrowserPackage()
    override fun getBookmarkDisplayTypeFlow(): Flow<String?> = bookMarkerDatastore.getBookmarkDisplayType()
    override fun getAppThemeModeFlow(): Flow<String?> = bookMarkerDatastore.getAppThemeMode()

    override suspend fun setDefaultBrowserPackage(packageName: String) {
        bookMarkerDatastore.saveDefaultBrowserPackage(packageName)
    }

    override suspend fun setBookmarkDisplayType(displayType: String) {
        bookMarkerDatastore.saveBookmarkDisplayType(displayType)
    }

    override suspend fun setAppThemeMode(mode: String) {
        bookMarkerDatastore.saveAppThemeMode(mode)
    }
}
