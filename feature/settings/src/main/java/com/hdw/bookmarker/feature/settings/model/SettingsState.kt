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
    val bookmarkDisplayType: String? = null,
    val showBookmarkUrl: Boolean = true,
    val scrollLongBookmarkUrl: Boolean = true,
    val showFolderDescription: Boolean = true,
    val scrollLongFolderDescription: Boolean = true,
    val folderIconStyle: BookmarkFolderIconStyle = BookmarkFolderIconStyle(),
    val appUpdateUiState: AppUpdateUiState = AppUpdateUiState.Checking,
    val updateLaunchRequestId: Long = 0L,
) : MavericksState
