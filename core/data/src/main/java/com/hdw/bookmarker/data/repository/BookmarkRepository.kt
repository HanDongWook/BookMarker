package com.hdw.bookmarker.data.repository

import com.hdw.bookmarker.model.Browser

interface BookmarkRepository {
    fun getBookMarks(browser: Browser)
}