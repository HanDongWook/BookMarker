package com.hdw.bookmarker.feature.importguide.domain.model

import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.model.browser.BrowserInfo

enum class BrowserGuideLinkType {
    OFFICIAL,
    IN_APP,
    NONE,
}

data class BrowserGuideItem(
    val browser: Browser,
    val displayName: String,
    val installedBrowser: BrowserInfo? = null,
    val guideLinkType: BrowserGuideLinkType = BrowserGuideLinkType.NONE,
    val requiresDesktopExport: Boolean = true,
) {
    val isInstalled: Boolean
        get() = installedBrowser != null

    val hasGuideLink: Boolean
        get() = guideLinkType != BrowserGuideLinkType.NONE
}
