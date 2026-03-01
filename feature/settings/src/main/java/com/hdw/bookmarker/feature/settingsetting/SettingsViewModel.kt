package com.hdw.bookmarker.feature.settingsetting

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.hdw.bookmarker.core.domain.usecase.GetAppThemeModeUseCase
import com.hdw.bookmarker.core.domain.usecase.GetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.domain.usecase.SetAppThemeModeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetDefaultBrowserPackageUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel @AssistedInject constructor(
    @Assisted initialState: SettingsState,
    private val getAppThemeModeUseCase: GetAppThemeModeUseCase,
    private val getDefaultBrowserPackageUseCase: GetDefaultBrowserPackageUseCase,
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
    private val setAppThemeModeUseCase: SetAppThemeModeUseCase,
    private val setDefaultBrowserPackageUseCase: SetDefaultBrowserPackageUseCase,
) : MavericksViewModel<SettingsState>(initialState) {
    private var observingAppTheme = false
    private var observingDefaultBrowser = false

    fun initialize(appVersion: String) {
        val installedBrowsers = getInstalledBrowsersUseCase()
        withState { _ ->
            setState {
                copy(
                    appVersion = appVersion,
                    installedBrowsers = installedBrowsers,
                    selectedBrowserPackage = selectedBrowserPackage
                        ?.takeIf { selected -> installedBrowsers.any { it.packageName == selected } }
                        ?: installedBrowsers.firstOrNull()?.packageName,
                )
            }
        }
        observeAppThemeMode()
        observeDefaultBrowser()
    }

    fun selectAppThemeMode(mode: String) {
        setState { copy(selectedThemeMode = mode) }
        viewModelScope.launch {
            setAppThemeModeUseCase(mode)
        }
    }

    fun selectDefaultBrowser(packageName: String) {
        setState { copy(selectedBrowserPackage = packageName) }
        viewModelScope.launch {
            setDefaultBrowserPackageUseCase(packageName)
        }
    }

    private fun observeAppThemeMode() {
        if (observingAppTheme) return
        observingAppTheme = true
        viewModelScope.launch {
            getAppThemeModeUseCase().collectLatest { persistedThemeMode ->
                withState { current ->
                    if (persistedThemeMode == current.selectedThemeMode) return@withState
                    setState { copy(selectedThemeMode = persistedThemeMode) }
                }
            }
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
