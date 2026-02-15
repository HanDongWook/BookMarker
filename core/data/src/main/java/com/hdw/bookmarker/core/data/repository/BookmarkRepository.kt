package com.hdw.bookmarker.core.data.repository

import com.hdw.bookmarker.core.model.browser.Browser

interface BookmarkRepository {
    fun getBookMarks(browser: Browser)
}