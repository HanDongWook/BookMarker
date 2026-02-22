package com.hdw.bookmarker.feature.settingsetting

import com.airbnb.mvrx.MavericksState
import com.hdw.bookmarker.core.ui.util.InstalledBrowserInfo

data class SettingsState(
    val appVersion: String = "-",
    val installedBrowsers: List<InstalledBrowserInfo> = emptyList(),
    val selectedBrowserPackage: String? = null,
) : MavericksState
