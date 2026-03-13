package com.hdw.bookmarker.feature.settings

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.hdw.bookmarker.core.domain.usecase.GetAppThemeModeUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkDisplayTypeUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkFolderIconStyleUseCase
import com.hdw.bookmarker.core.domain.usecase.GetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.domain.usecase.GetScrollLongBookmarkUrlUseCase
import com.hdw.bookmarker.core.domain.usecase.GetScrollLongFolderDescriptionUseCase
import com.hdw.bookmarker.core.domain.usecase.GetShowBookmarkUrlUseCase
import com.hdw.bookmarker.core.domain.usecase.GetShowFolderDescriptionUseCase
import com.hdw.bookmarker.core.domain.usecase.SetAppThemeModeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkDisplayTypeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkFolderIconColorUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkFolderIconShapeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.domain.usecase.SetScrollLongBookmarkUrlUseCase
import com.hdw.bookmarker.core.domain.usecase.SetScrollLongFolderDescriptionUseCase
import com.hdw.bookmarker.core.domain.usecase.SetShowBookmarkUrlUseCase
import com.hdw.bookmarker.core.domain.usecase.SetShowFolderDescriptionUseCase
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.feature.settings.model.SettingsState
import com.hdw.bookmarker.feature.settings.ui.tab.appversion.AppUpdateUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel @AssistedInject constructor(
    @Assisted initialState: SettingsState,
    private val getAppThemeModeUseCase: GetAppThemeModeUseCase,
    private val getBookmarkDisplayTypeUseCase: GetBookmarkDisplayTypeUseCase,
    private val getDefaultBrowserPackageUseCase: GetDefaultBrowserPackageUseCase,
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
    private val getScrollLongBookmarkUrlUseCase: GetScrollLongBookmarkUrlUseCase,
    private val getShowBookmarkUrlUseCase: GetShowBookmarkUrlUseCase,
    private val getScrollLongFolderDescriptionUseCase: GetScrollLongFolderDescriptionUseCase,
    private val getShowFolderDescriptionUseCase: GetShowFolderDescriptionUseCase,
    private val setAppThemeModeUseCase: SetAppThemeModeUseCase,
    private val setBookmarkDisplayTypeUseCase: SetBookmarkDisplayTypeUseCase,
    private val setScrollLongBookmarkUrlUseCase: SetScrollLongBookmarkUrlUseCase,
    private val setShowBookmarkUrlUseCase: SetShowBookmarkUrlUseCase,
    private val setScrollLongFolderDescriptionUseCase: SetScrollLongFolderDescriptionUseCase,
    private val setShowFolderDescriptionUseCase: SetShowFolderDescriptionUseCase,
    private val setDefaultBrowserPackageUseCase: SetDefaultBrowserPackageUseCase,
    private val getBookmarkFolderIconStyleUseCase: GetBookmarkFolderIconStyleUseCase,
    private val setBookmarkFolderIconShapeUseCase: SetBookmarkFolderIconShapeUseCase,
    private val setBookmarkFolderIconColorUseCase: SetBookmarkFolderIconColorUseCase,
) : MavericksViewModel<SettingsState>(initialState) {
    private var observingAppTheme = false
    private var observingBookmarkDisplayType = false
    private var observingScrollLongBookmarkUrl = false
    private var observingShowBookmarkUrl = false
    private var observingShowFolderDescription = false
    private var observingScrollLongFolderDescription = false
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
        observeBookmarkDisplayType()
        observeScrollLongBookmarkUrl()
        observeShowBookmarkUrl()
        observeShowFolderDescription()
        observeScrollLongFolderDescription()
        observeDefaultBrowser()
        observeFolderIconStyle()
    }

    fun selectAppThemeMode(mode: String) {
        setState { copy(selectedThemeMode = mode) }
        viewModelScope.launch {
            setAppThemeModeUseCase(mode)
        }
    }

    fun selectBookmarkDisplayType(displayType: String) {
        setState { copy(bookmarkDisplayType = displayType) }
        viewModelScope.launch {
            setBookmarkDisplayTypeUseCase(displayType)
        }
    }

    fun setShowBookmarkUrl(show: Boolean) {
        setState { copy(showBookmarkUrl = show) }
        viewModelScope.launch {
            setShowBookmarkUrlUseCase(show)
        }
    }

    fun setScrollLongBookmarkUrl(enabled: Boolean) {
        setState { copy(scrollLongBookmarkUrl = enabled) }
        viewModelScope.launch {
            setScrollLongBookmarkUrlUseCase(enabled)
        }
    }

    fun setShowFolderDescription(show: Boolean) {
        setState { copy(showFolderDescription = show) }
        viewModelScope.launch {
            setShowFolderDescriptionUseCase(show)
        }
    }

    fun setScrollLongFolderDescription(enabled: Boolean) {
        setState { copy(scrollLongFolderDescription = enabled) }
        viewModelScope.launch {
            setScrollLongFolderDescriptionUseCase(enabled)
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

    fun setAppUpdateUiState(appUpdateUiState: AppUpdateUiState) {
        withState { current ->
            if (current.appUpdateUiState == appUpdateUiState) return@withState
            setState { copy(appUpdateUiState = appUpdateUiState) }
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

    private fun observeBookmarkDisplayType() {
        if (observingBookmarkDisplayType) return
        observingBookmarkDisplayType = true
        viewModelScope.launch {
            getBookmarkDisplayTypeUseCase().collectLatest { persistedDisplayType ->
                withState { current ->
                    if (persistedDisplayType == current.bookmarkDisplayType) return@withState
                    setState { copy(bookmarkDisplayType = persistedDisplayType) }
                }
            }
        }
    }

    private fun observeShowBookmarkUrl() {
        if (observingShowBookmarkUrl) return
        observingShowBookmarkUrl = true
        viewModelScope.launch {
            getShowBookmarkUrlUseCase().collectLatest { showBookmarkUrl ->
                withState { current ->
                    if (showBookmarkUrl == current.showBookmarkUrl) return@withState
                    setState { copy(showBookmarkUrl = showBookmarkUrl) }
                }
            }
        }
    }

    private fun observeScrollLongBookmarkUrl() {
        if (observingScrollLongBookmarkUrl) return
        observingScrollLongBookmarkUrl = true
        viewModelScope.launch {
            getScrollLongBookmarkUrlUseCase().collectLatest { scrollLongBookmarkUrl ->
                withState { current ->
                    if (scrollLongBookmarkUrl == current.scrollLongBookmarkUrl) return@withState
                    setState { copy(scrollLongBookmarkUrl = scrollLongBookmarkUrl) }
                }
            }
        }
    }

    private fun observeShowFolderDescription() {
        if (observingShowFolderDescription) return
        observingShowFolderDescription = true
        viewModelScope.launch {
            getShowFolderDescriptionUseCase().collectLatest { showFolderDescription ->
                withState { current ->
                    if (showFolderDescription == current.showFolderDescription) return@withState
                    setState { copy(showFolderDescription = showFolderDescription) }
                }
            }
        }
    }

    private fun observeScrollLongFolderDescription() {
        if (observingScrollLongFolderDescription) return
        observingScrollLongFolderDescription = true
        viewModelScope.launch {
            getScrollLongFolderDescriptionUseCase().collectLatest { scrollLongFolderDescription ->
                withState { current ->
                    if (scrollLongFolderDescription == current.scrollLongFolderDescription) return@withState
                    setState { copy(scrollLongFolderDescription = scrollLongFolderDescription) }
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
