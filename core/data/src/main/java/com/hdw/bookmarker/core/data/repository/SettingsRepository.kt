package com.hdw.bookmarker.core.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getDefaultBrowserPackageFlow(): Flow<String?>
    fun getBookmarkDisplayTypeFlow(): Flow<String?>

    suspend fun setDefaultBrowserPackage(packageName: String)
    suspend fun setBookmarkDisplayType(displayType: String)
}
