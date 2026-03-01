package com.hdw.bookmarker.feature.settingsetting

import com.airbnb.mvrx.MavericksState
import com.hdw.bookmarker.core.model.browser.BrowserInfo

data class SettingsState(
    val appVersion: String = "-",
    val installedBrowsers: List<BrowserInfo> = emptyList(),
    val selectedBrowserPackage: String? = null,
    val selectedThemeMode: String? = null,
) : MavericksState
