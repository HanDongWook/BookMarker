package com.hdw.bookmarker.feature.settings.presentation

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.hdw.bookmarker.core.domain.usecase.GetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.domain.usecase.SetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.feature.settings.presentation.component.tab.appversion.AppUpdateUiState
import com.hdw.bookmarker.feature.settings.presentation.model.DisplayValueState
import com.hdw.bookmarker.feature.settings.presentation.model.SettingsState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel @AssistedInject constructor(
    @Assisted initialState: SettingsState,
    private val getDefaultBrowserPackageUseCase: GetDefaultBrowserPackageUseCase,
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
    private val setDefaultBrowserPackageUseCase: SetDefaultBrowserPackageUseCase,
) : MavericksViewModel<SettingsState>(initialState) {
    private var observingDefaultBrowser = false

    suspend fun initialize(appVersion: String?) {
        val installedBrowsers = getInstalledBrowsersUseCase()
        withState { _ ->
            setState {
                copy(
                    appVersion = appVersion
                        ?.takeIf { it.isNotBlank() }
                        ?.let(DisplayValueState::Loaded)
                        ?: DisplayValueState.Unavailable,
                    installedBrowsers = installedBrowsers,
                    selectedBrowserPackage = selectedBrowserPackage
                        ?.takeIf { selected -> installedBrowsers.any { it.packageName == selected } }
                        ?: installedBrowsers.firstOrNull()?.packageName,
                )
            }
        }
        observeDefaultBrowser()
    }

    fun selectDefaultBrowser(packageName: String) {
        setState { copy(selectedBrowserPackage = packageName) }
        viewModelScope.launch {
            setDefaultBrowserPackageUseCase(packageName)
        }
    }

    fun setAppUpdateUiState(appUpdateUiState: AppUpdateUiState) {
        withState { current ->
            if (current.appUpdateUiState == appUpdateUiState) return@withState
            setState { copy(appUpdateUiState = appUpdateUiState) }
        }
    }

    private fun observeDefaultBrowser() {
        if (observingDefaultBrowser) return
        observingDefaultBrowser = true
        viewModelScope.launch {
            getDefaultBrowserPackageUseCase().collectLatest { persistedSelectedBrowserPackage ->
                withState { current ->
                    val nextSelection = persistedSelectedBrowserPackage
                        ?.packageName
                        ?: current.selectedBrowserPackage
                        ?: current.installedBrowsers.firstOrNull()?.packageName
                    if (nextSelection == current.selectedBrowserPackage) return@withState
                    setState { copy(selectedBrowserPackage = nextSelection) }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<SettingsViewModel, SettingsState> {
        override fun create(state: SettingsState): SettingsViewModel
    }

    companion object : MavericksViewModelFactory<SettingsViewModel, SettingsState> by hiltMavericksViewModelFactory()
}
