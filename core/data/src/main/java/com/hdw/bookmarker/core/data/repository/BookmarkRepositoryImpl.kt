package com.hdw.bookmarker.core.data.repository

import com.hdw.bookmarker.core.data.bookmarker.chrome.BookmarkManager
import com.hdw.bookmarker.core.model.browser.Browser
import timber.log.Timber
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkManager: BookmarkManager
) : BookmarkRepository {
    override fun getBookMarks(browser: Browser) {
        Timber.d("getBookMarks browser:$browser")
        when(browser) {
            Browser.CHROME -> handleChromeBookMarker()
            else -> Unit // todo add handle other browsers
        }
    }

    private fun handleChromeBookMarker() {
        bookmarkManager.openChromeBookmarks()
    }
}