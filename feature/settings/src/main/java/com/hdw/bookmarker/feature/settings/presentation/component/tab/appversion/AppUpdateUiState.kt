package com.hdw.bookmarker.feature.settings.presentation.component.tab.appversion

sealed interface AppUpdateUiState {
    data object Checking : AppUpdateUiState

    data object UpToDate : AppUpdateUiState

    data class UpdateAvailable(val availableVersionCode: Int) : AppUpdateUiState

    data object InProgress : AppUpdateUiState

    data object Unavailable : AppUpdateUiState
}
