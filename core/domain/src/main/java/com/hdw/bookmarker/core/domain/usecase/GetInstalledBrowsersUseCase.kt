package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.BrowserRepository
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import javax.inject.Inject

class GetInstalledBrowsersUseCase @Inject constructor(private val browserRepository: BrowserRepository) {
    operator fun invoke(): List<BrowserInfo> = browserRepository.getInstalledBrowsers()
}
