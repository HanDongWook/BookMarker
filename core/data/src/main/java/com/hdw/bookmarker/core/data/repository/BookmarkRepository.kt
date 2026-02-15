package com.hdw.bookmarker.core.data.repository

import com.hdw.bookmarker.core.model.Browser

interface BookmarkRepository {
    fun getBookMarks(browser: Browser)
}