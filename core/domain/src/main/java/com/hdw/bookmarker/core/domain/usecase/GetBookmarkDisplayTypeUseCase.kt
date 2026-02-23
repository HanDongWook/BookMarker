package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarkDisplayTypeUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    operator fun invoke(): Flow<String?> = settingsRepository.getBookmarkDisplayTypeFlow()
}
