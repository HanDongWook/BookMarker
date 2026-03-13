package com.hdw.bookmarker.feature.settings.model

import com.airbnb.mvrx.MavericksState
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import com.hdw.bookmarker.feature.settings.ui.tab.appversion.AppUpdateUiState

data class SettingsState(
    val appVersion: String = "-",
    val installedBrowsers: List<BrowserInfo> = emptyList(),
    val selectedBrowserPackage: String? = null,
    val appUpdateUiState: AppUpdateUiState = AppUpdateUiState.Checking,
) : MavericksState
