package com.hdw.bookmarker.data.repository

import com.hdw.bookmarker.model.Browser
import timber.log.Timber
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor() : BookmarkRepository {
    override fun getBookMarks(browser: Browser) {
        Timber.e("getBookMarks")
    }
}