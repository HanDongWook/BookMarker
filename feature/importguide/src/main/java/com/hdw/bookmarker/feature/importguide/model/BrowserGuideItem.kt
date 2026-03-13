package com.hdw.bookmarker.feature.importguide.model

import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.model.browser.BrowserInfo

enum class BrowserGuideLinkType {
    OFFICIAL,
    IN_APP,
    NONE,
}

enum class BrowserGuideFilter {
    ALL,
    INSTALLED,
    GUIDE,
    DESKTOP,
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

    fun matches(filter: BrowserGuideFilter): Boolean = when (filter) {
        BrowserGuideFilter.ALL -> true
        BrowserGuideFilter.INSTALLED -> isInstalled
        BrowserGuideFilter.GUIDE -> hasGuideLink
        BrowserGuideFilter.DESKTOP -> requiresDesktopExport
    }
}
