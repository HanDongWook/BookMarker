package com.hdw.bookmarker.feature.settings

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.hdw.bookmarker.core.domain.usecase.GetAppThemeModeUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkFolderIconColorUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkFolderIconShapeUseCase
import com.hdw.bookmarker.core.domain.usecase.GetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.domain.usecase.SetAppThemeModeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkFolderIconColorUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkFolderIconShapeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconShape
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
    val selectedFolderIconShape: BookmarkFolderIconShape = BookmarkFolderIconShape.FILLED,
    val selectedFolderIconColor: BookmarkFolderIconColor = BookmarkFolderIconColor.DEFAULT,
) : MavericksState

class SettingsViewModel @AssistedInject constructor(
    @Assisted initialState: SettingsState,
    private val getAppThemeModeUseCase: GetAppThemeModeUseCase,
    private val getDefaultBrowserPackageUseCase: GetDefaultBrowserPackageUseCase,
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
    private val setAppThemeModeUseCase: SetAppThemeModeUseCase,
    private val setDefaultBrowserPackageUseCase: SetDefaultBrowserPackageUseCase,
    private val getBookmarkFolderIconShapeUseCase: GetBookmarkFolderIconShapeUseCase,
    private val setBookmarkFolderIconShapeUseCase: SetBookmarkFolderIconShapeUseCase,
    private val getBookmarkFolderIconColorUseCase: GetBookmarkFolderIconColorUseCase,
    private val setBookmarkFolderIconColorUseCase: SetBookmarkFolderIconColorUseCase,
) : MavericksViewModel<SettingsState>(initialState) {
    private var observingAppTheme = false
    private var observingDefaultBrowser = false
    private var observingFolderIconShape = false
    private var observingFolderIconColor = false

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
        observeFolderIconShape()
        observeFolderIconColor()
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
        setState { copy(selectedFolderIconShape = shape) }
        viewModelScope.launch {
            setBookmarkFolderIconShapeUseCase(shape.name)
        }
    }

    fun selectFolderIconColor(color: BookmarkFolderIconColor) {
        setState { copy(selectedFolderIconColor = color) }
        viewModelScope.launch {
            setBookmarkFolderIconColorUseCase(color.name)
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

    private fun observeFolderIconShape() {
        if (observingFolderIconShape) return
        observingFolderIconShape = true
        viewModelScope.launch {
            getBookmarkFolderIconShapeUseCase().collectLatest { persisted ->
                val shape = BookmarkFolderIconShape.fromPersisted(persisted)
                withState { current ->
                    if (shape == current.selectedFolderIconShape) return@withState
                    setState { copy(selectedFolderIconShape = shape) }
                }
            }
        }
    }

    private fun observeFolderIconColor() {
        if (observingFolderIconColor) return
        observingFolderIconColor = true
        viewModelScope.launch {
            getBookmarkFolderIconColorUseCase().collectLatest { persisted ->
                val color = BookmarkFolderIconColor.fromPersisted(persisted)
                withState { current ->
                    if (color == current.selectedFolderIconColor) return@withState
                    setState { copy(selectedFolderIconColor = color) }
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
