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
import timber.log.Timber
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val chromeBookmarkManager: ChromeBookmarkManager,
    private val contentFileManager: ContentFileManager,
    private val bookmarkSnapshotDatastore: BookMarkerBookmarkSnapshotDatastore,
) : BookmarkRepository {

    override fun getBookMarks(browser: Browser, uri: Uri): BookmarkImportResult {
        Timber.d("getBookMarks browser:$browser")
        return when (browser) {
            Browser.CHROME -> handleChromeBookMarker(uri)

            else -> BookmarkImportResult.Failure(
                error = BookmarkImportError.UNSUPPORTED_BROWSER,
            )
        }
    }

    private fun handleChromeBookMarker(uri: Uri): BookmarkImportResult = chromeBookmarkManager.parsingHtml(uri)

    override fun getRawFileHash(uri: Uri): ContentFileResult<String> = contentFileManager.getRawFileHash(uri)

    override fun getBookmarkSnapshotsFlow(): Flow<Map<String, BookmarkDocument>> =
        bookmarkSnapshotDatastore.getSnapshotsFlow()

    override suspend fun saveBookmarkSnapshot(browserPackage: String, document: BookmarkDocument, sourceHash: String) {
        bookmarkSnapshotDatastore.saveSnapshot(
            browserPackage = browserPackage,
            document = document,
            sourceHash = sourceHash,
        )
    }

    override suspend fun clearBookmarkSnapshot(browserPackage: String) {
        bookmarkSnapshotDatastore.clearSnapshot(browserPackage)
    }

    override suspend fun getBookmarkSnapshotRawFileHash(browserPackage: String): String? =
        bookmarkSnapshotDatastore.getRawFileHash(browserPackage)
}
