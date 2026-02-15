package com.hdw.bookmarker.core.data.repository

import com.hdw.bookmarker.core.model.Browser
import timber.log.Timber
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor() : BookmarkRepository {
    override fun getBookMarks(browser: Browser) {
        Timber.e("getBookMarks")
    }
}