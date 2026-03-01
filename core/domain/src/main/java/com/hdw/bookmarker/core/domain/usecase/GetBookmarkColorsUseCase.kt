package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.BrowserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarkColorsUseCase @Inject constructor(private val browserRepository: BrowserRepository) {
    operator fun invoke(): Flow<Map<String, Long>> = browserRepository.getBookmarkColors()
}
