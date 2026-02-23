package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import javax.inject.Inject

class ClearBookmarkSnapshotUseCase @Inject constructor(private val bookmarkRepository: BookmarkRepository) {
    suspend operator fun invoke(browserPackage: String) {
        bookmarkRepository.clearBookmarkSnapshot(browserPackage)
    }
}
