package com.hdw.bookmarker.core.data.repository

import android.net.Uri
import com.hdw.bookmarker.core.data.bookmark.chrome.ChromeBookmarkManager
import com.hdw.bookmarker.core.data.file.ContentFileManager
import com.hdw.bookmarker.core.datastore.bookmark.BookMarkerBookmarkSnapshotDatastore
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.error.BookmarkImportError
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.model.file.result.ContentFileResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val chromeBookmarkManager: ChromeBookmarkManager,
    private val contentFileManager: ContentFileManager,
    private val bookmarkSnapshotDatastore: BookMarkerBookmarkSnapshotDatastore,
) : BookmarkRepository {

    override fun getBookmarks(browser: Browser, uri: Uri): BookmarkImportResult {
        Timber.d("getBookmarks browser:$browser")
        return when (browser) {
            Browser.CHROME -> handleChromeBookmark(uri)

            else -> BookmarkImportResult.Failure(
                error = BookmarkImportError.UNSUPPORTED_BROWSER,
            )
        }
    }

    private fun handleChromeBookmark(uri: Uri): BookmarkImportResult = chromeBookmarkManager.parseHtml(uri)

    override fun getRawFileHash(uri: Uri): ContentFileResult<String> = contentFileManager.getRawFileHash(uri)

    override fun getBookmarkSnapshotsFlow(): Flow<Map<String, BookmarkDocument>> =
        bookmarkSnapshotDatastore.getSnapshotsFlow()

    override fun getOrderedSnapshotIdsFlow(): Flow<List<String>> = bookmarkSnapshotDatastore.getOrderedSnapshotIdsFlow()

    override suspend fun saveBookmarkSnapshot(
        snapshotId: String?,
        document: BookmarkDocument,
        sourceHash: String,
        bookmarkColor: Long,
    ): String = bookmarkSnapshotDatastore.saveSnapshot(
        snapshotId = snapshotId,
        document = document,
        sourceHash = sourceHash,
        bookmarkColor = bookmarkColor,
    )

    override suspend fun clearBookmarkSnapshot(snapshotId: String) {
        bookmarkSnapshotDatastore.clearSnapshot(snapshotId)
    }

    override suspend fun getBookmarkSnapshotRawFileHash(snapshotId: String): String? =
        bookmarkSnapshotDatastore.getRawFileHash(snapshotId)

    override suspend fun getBookmarkColor(snapshotId: String): Long? =
        bookmarkSnapshotDatastore.getBookmarkColorsFlow().first()[snapshotId]

    override suspend fun setBookmarkColor(snapshotId: String, bookmarkColor: Long) {
        bookmarkSnapshotDatastore.updateBookmarkColor(
            snapshotId = snapshotId,
            bookmarkColor = bookmarkColor,
        )
    }
}
