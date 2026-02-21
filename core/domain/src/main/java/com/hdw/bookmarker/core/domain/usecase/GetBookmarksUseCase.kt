package com.hdw.bookmarker.core.domain.usecase

import android.net.Uri
import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import com.hdw.bookmarker.core.model.browser.Browser
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(private val bookmarkRepository: BookmarkRepository) {
    operator fun invoke(browser: Browser, uri: Uri): BookmarkImportResult =
        bookmarkRepository.getBookMarks(browser = browser, uri = uri)
}
