package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrderedSnapshotIdsUseCase @Inject constructor(private val bookmarkRepository: BookmarkRepository) {
    operator fun invoke(): Flow<List<String>> = bookmarkRepository.getOrderedSnapshotIdsFlow()
}
