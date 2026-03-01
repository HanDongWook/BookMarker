package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import javax.inject.Inject

class SetBookmarkColorUseCase @Inject constructor(private val bookmarkRepository: BookmarkRepository) {
    suspend operator fun invoke(browserPackage: String, bookmarkColor: Long) {
        bookmarkRepository.setBookmarkColor(browserPackage, bookmarkColor)
    }
}
