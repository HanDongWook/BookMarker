package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.SettingsRepository
import javax.inject.Inject

class SetScrollLongBookmarkUrlUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(enabled: Boolean) {
        settingsRepository.setScrollLongBookmarkUrl(enabled)
    }
}
