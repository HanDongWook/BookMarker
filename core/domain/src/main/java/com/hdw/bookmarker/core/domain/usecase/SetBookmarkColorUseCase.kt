package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import javax.inject.Inject

class SetBookmarkColorUseCase @Inject constructor(private val bookmarkRepository: BookmarkRepository) {
    suspend operator fun invoke(snapshotId: String, bookmarkColor: Long) {
        bookmarkRepository.setBookmarkColor(snapshotId, bookmarkColor)
    }
}
