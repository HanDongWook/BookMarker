package com.hdw.bookmarker.feature.importguide.model

import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.model.browser.BrowserInfo

data class BrowserGuideItem(
    val browser: Browser,
    val displayName: String,
    val installedBrowser: BrowserInfo? = null,
) {
    val isInstalled: Boolean
        get() = installedBrowser != null
}
