package com.hdw.bookmarker.core.data.repository

import android.net.Uri
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.model.file.result.ContentFileResult
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getBookMarks(browser: Browser, uri: Uri): BookmarkImportResult

    fun getRawFileHash(uri: Uri): ContentFileResult<String>

    fun getBookmarkSnapshotsFlow(): Flow<Map<String, BookmarkDocument>>

    suspend fun saveBookmarkSnapshot(browserPackage: String, document: BookmarkDocument, sourceHash: String = "")

    suspend fun clearBookmarkSnapshot(browserPackage: String)

    suspend fun getBookmarkSnapshotRawFileHash(browserPackage: String): String?
}
