package com.hdw.bookmarker.feature.settings.model

import com.airbnb.mvrx.MavericksState
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.feature.settings.ui.tab.appversion.AppUpdateUiState

data class SettingsState(
    val appVersion: String = "-",
    val installedBrowsers: List<BrowserInfo> = emptyList(),
    val selectedBrowserPackage: String? = null,
    val selectedThemeMode: String? = null,
    val folderIconStyle: BookmarkFolderIconStyle = BookmarkFolderIconStyle(),
    val appUpdateUiState: AppUpdateUiState = AppUpdateUiState.Checking,
    val updateLaunchRequestId: Long = 0L,
) : MavericksState
