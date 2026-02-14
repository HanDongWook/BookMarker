package com.hdw.bookmarker.domain.usecase

import com.hdw.bookmarker.data.repository.BookmarkRepository
import com.hdw.bookmarker.model.Browser
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke(browser: Browser) =
        bookmarkRepository.getBookMarks(browser = Browser.CHROME)
}
