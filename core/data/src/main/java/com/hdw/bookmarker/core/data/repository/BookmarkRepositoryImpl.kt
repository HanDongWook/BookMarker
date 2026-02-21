package com.hdw.bookmarker.core.data.repository

import android.net.Uri
import com.hdw.bookmarker.core.data.bookmark.chrome.ChromeBookmarkManager
import com.hdw.bookmarker.core.model.bookmark.error.BookmarkImportError
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import com.hdw.bookmarker.core.model.browser.Browser
import timber.log.Timber
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val chromeBookmarkManager: ChromeBookmarkManager
) : BookmarkRepository {
    override fun getBookMarks(browser: Browser, uri: Uri): BookmarkImportResult {
        Timber.d("getBookMarks browser:$browser")
        return when(browser) {
            Browser.CHROME -> handleChromeBookMarker(uri)
            else -> BookmarkImportResult.Failure(
                error = BookmarkImportError.UNSUPPORTED_BROWSER
            )
        }
    }

    private fun handleChromeBookMarker(uri: Uri): BookmarkImportResult {
        return chromeBookmarkManager.parsingHtml(uri)
    }
}
