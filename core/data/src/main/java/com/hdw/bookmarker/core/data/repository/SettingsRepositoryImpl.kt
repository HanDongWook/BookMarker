package com.hdw.bookmarker.core.data.repository

import com.hdw.bookmarker.core.datastore.preferences.BookMarkerDatastore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(private val bookMarkerDatastore: BookMarkerDatastore) :
    SettingsRepository {

    override fun getDefaultBrowserPackageFlow(): Flow<String?> = bookMarkerDatastore.getDefaultBrowserPackage()

    override suspend fun setDefaultBrowserPackage(packageName: String) {
        bookMarkerDatastore.saveDefaultBrowserPackage(packageName)
    }
}
