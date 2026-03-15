package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.SettingsRepository
import javax.inject.Inject

class SetBookmarkSecondaryDisplayTypeUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(displayType: String) {
        settingsRepository.setBookmarkSecondaryDisplayType(displayType)
    }
}
