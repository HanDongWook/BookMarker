package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.SettingsRepository
import javax.inject.Inject

class SetDefaultBrowserPackageUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(packageName: String) {
        settingsRepository.setDefaultBrowserPackage(packageName)
    }
}
