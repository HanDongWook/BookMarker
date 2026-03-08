package com.hdw.bookmarker.feature.settings

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.hdw.bookmarker.core.domain.usecase.GetAppThemeModeUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkFolderIconStyleUseCase
import com.hdw.bookmarker.core.domain.usecase.GetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.domain.usecase.SetAppThemeModeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkFolderIconColorUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkFolderIconShapeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.feature.settings.appversion.AppUpdateUiState
import com.hdw.bookmarker.feature.settings.appversion.requestAppUpdateState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SettingsState(
    val appVersion: String = "-",
    val installedBrowsers: List<BrowserInfo> = emptyList(),
    val selectedBrowserPackage: String? = null,
    val selectedThemeMode: String? = null,
    val folderIconStyle: BookmarkFolderIconStyle = BookmarkFolderIconStyle(),
    val appUpdateUiState: AppUpdateUiState = AppUpdateUiState.Checking,
    val updateLaunchRequestId: Long = 0L,
) : MavericksState

class SettingsViewModel @AssistedInject constructor(
    @Assisted initialState: SettingsState,
    private val getAppThemeModeUseCase: GetAppThemeModeUseCase,
    private val getDefaultBrowserPackageUseCase: GetDefaultBrowserPackageUseCase,
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
    private val setAppThemeModeUseCase: SetAppThemeModeUseCase,
    private val setDefaultBrowserPackageUseCase: SetDefaultBrowserPackageUseCase,
    private val getBookmarkFolderIconStyleUseCase: GetBookmarkFolderIconStyleUseCase,
    private val setBookmarkFolderIconShapeUseCase: SetBookmarkFolderIconShapeUseCase,
    private val setBookmarkFolderIconColorUseCase: SetBookmarkFolderIconColorUseCase,
) : MavericksViewModel<SettingsState>(initialState) {
    private var observingAppTheme = false
    private var observingDefaultBrowser = false
    private var observingFolderIconStyle = false

    suspend fun initialize(appVersion: String) {
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
        observeFolderIconStyle()
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

    fun selectFolderIconShape(shape: BookmarkFolderIconShape) {
        setState { copy(folderIconStyle = folderIconStyle.copy(shape = shape)) }
        viewModelScope.launch {
            setBookmarkFolderIconShapeUseCase(shape.name)
        }
    }

    fun selectFolderIconColor(color: BookmarkFolderIconColor) {
        setState { copy(folderIconStyle = folderIconStyle.copy(color = color)) }
        viewModelScope.launch {
            setBookmarkFolderIconColorUseCase(color.name)
        }
    }

    fun fetchAppUpdateState(appUpdateManager: AppUpdateManager, onPendingUpdateInfo: (AppUpdateInfo?) -> Unit) {
        setState { copy(appUpdateUiState = AppUpdateUiState.Checking) }
        viewModelScope.launch {
            val updateStateResult = requestAppUpdateState(appUpdateManager)
            setState { copy(appUpdateUiState = updateStateResult.uiState) }
            onPendingUpdateInfo(updateStateResult.pendingUpdateInfo)
            if (updateStateResult.uiState is AppUpdateUiState.InProgress) {
                requestAppUpdateLaunch()
            }
        }
    }

    fun onAppUpdateClick() {
        withState { current ->
            if (
                current.appUpdateUiState is AppUpdateUiState.UpdateAvailable ||
                current.appUpdateUiState is AppUpdateUiState.InProgress
            ) {
                requestAppUpdateLaunch()
            }
        }
    }

    fun onAppUpdateLaunchResult(isStarted: Boolean) {
        setState {
            copy(appUpdateUiState = if (isStarted) AppUpdateUiState.InProgress else AppUpdateUiState.Unavailable)
        }
    }

    private fun requestAppUpdateLaunch() {
        setState { copy(updateLaunchRequestId = updateLaunchRequestId + 1) }
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

    private fun observeFolderIconStyle() {
        if (observingFolderIconStyle) return
        observingFolderIconStyle = true
        viewModelScope.launch {
            getBookmarkFolderIconStyleUseCase().collectLatest { nextFolderIconStyle ->
                withState { current ->
                    if (nextFolderIconStyle == current.folderIconStyle) {
                        return@withState
                    }
                    setState {
                        copy(folderIconStyle = nextFolderIconStyle)
                    }
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
