package com.hdw.bookmarker.domain.usecase

import com.hdw.bookmarker.domain.repository.BrowserRepository
import com.hdw.bookmarker.model.BrowserInfo
import javax.inject.Inject

class GetInstalledBrowsersUseCase @Inject constructor(private val browserRepository: BrowserRepository) {
    operator fun invoke(): List<BrowserInfo> = browserRepository.getInstalledBrowsers()
}
