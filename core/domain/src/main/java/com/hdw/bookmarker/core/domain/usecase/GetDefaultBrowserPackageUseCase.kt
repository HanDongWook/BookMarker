package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.BrowserRepository
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDefaultBrowserPackageUseCase @Inject constructor(
    private val browserRepository: BrowserRepository,
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): Flow<BrowserInfo?> = settingsRepository.getDefaultBrowserPackageFlow().map { packageName ->
        packageName?.let { targetPackage ->
            browserRepository.getInstalledBrowsers().firstOrNull { it.packageName == targetPackage }
        }
    }
}
