package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarkSnapshotsUseCase @Inject constructor(private val bookmarkRepository: BookmarkRepository) {
    operator fun invoke(): Flow<Map<String, BookmarkDocument>> = bookmarkRepository.getBookmarkSnapshotsFlow()
}
