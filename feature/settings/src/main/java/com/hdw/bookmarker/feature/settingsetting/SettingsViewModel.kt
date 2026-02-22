package com.hdw.bookmarker.feature.settingsetting

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.airbnb.mvrx.withState
import com.hdw.bookmarker.core.ui.util.InstalledBrowserInfo

class SettingsViewModel(initialState: SettingsState) : MavericksViewModel<SettingsState>(initialState) {

    fun initialize(
        appVersion: String,
        installedBrowsers: List<InstalledBrowserInfo>,
        persistedSelectedBrowserPackage: String?,
    ) {
        withState { current ->
            if (current.appVersion != "-" && current.installedBrowsers.isNotEmpty()) return@withState
            val initialSelection = persistedSelectedBrowserPackage
                ?.takeIf { persisted -> installedBrowsers.any { it.packageName == persisted } }
                ?: installedBrowsers.firstOrNull()?.packageName
            setState {
                copy(
                    appVersion = appVersion,
                    installedBrowsers = installedBrowsers,
                    selectedBrowserPackage = initialSelection,
                )
            }
        }
    }

    fun selectDefaultBrowser(packageName: String) {
        setState { copy(selectedBrowserPackage = packageName) }
    }

    fun syncPersistedSelectedBrowser(persistedSelectedBrowserPackage: String?) {
        withState { current ->
            val nextSelection = persistedSelectedBrowserPackage
                ?.takeIf { persisted -> current.installedBrowsers.any { it.packageName == persisted } }
                ?: current.selectedBrowserPackage
                ?: current.installedBrowsers.firstOrNull()?.packageName
            if (nextSelection == current.selectedBrowserPackage) return@withState
            setState { copy(selectedBrowserPackage = nextSelection) }
        }
    }

    companion object : MavericksViewModelFactory<SettingsViewModel, SettingsState> {
        override fun create(viewModelContext: ViewModelContext, state: SettingsState): SettingsViewModel =
            SettingsViewModel(state)
    }
}
