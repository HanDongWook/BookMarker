package com.hdw.bookmarker.feature.settingsetting

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext

class SettingsViewModel(
    initialState: SettingsState,
) : MavericksViewModel<SettingsState>(initialState) {

    fun setAppVersion(version: String) {
        setState { copy(appVersion = version) }
    }

    companion object : MavericksViewModelFactory<SettingsViewModel, SettingsState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: SettingsState,
        ): SettingsViewModel = SettingsViewModel(state)
    }
}
