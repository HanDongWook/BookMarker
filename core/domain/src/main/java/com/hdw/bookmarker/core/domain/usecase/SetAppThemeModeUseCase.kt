package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.SettingsRepository
import javax.inject.Inject

class SetAppThemeModeUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(mode: String) {
        settingsRepository.setAppThemeMode(mode)
    }
}
