package com.hdw.bookmarker.core.data.repository

import android.net.Uri
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import com.hdw.bookmarker.core.model.browser.Browser

interface BookmarkRepository {
    fun getBookMarks(browser: Browser, uri: Uri): BookmarkImportResult
}
