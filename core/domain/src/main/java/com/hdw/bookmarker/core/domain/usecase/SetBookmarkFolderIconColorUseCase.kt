package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.SettingsRepository
import javax.inject.Inject

class SetBookmarkFolderIconColorUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(color: String) {
        settingsRepository.setBookmarkFolderIconColor(color)
    }
}
