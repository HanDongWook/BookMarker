package com.hdw.bookmarker.feature.settings.appearance

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.hdw.bookmarker.core.domain.usecase.ObserveAppearanceSettingsUseCase
import com.hdw.bookmarker.core.domain.usecase.SetAppThemeModeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkFolderIconColorUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkFolderIconShapeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetOpenBookmarkAdjacentOnLargeScreenUseCase
import com.hdw.bookmarker.core.domain.usecase.SetScrollLongBookmarkUrlUseCase
import com.hdw.bookmarker.core.domain.usecase.SetScrollLongFolderDescriptionUseCase
import com.hdw.bookmarker.core.domain.usecase.SetShowBookmarkUrlUseCase
import com.hdw.bookmarker.core.domain.usecase.SetShowFolderDescriptionUseCase
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.feature.settings.model.appearance.AppearanceState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AppearanceViewModel @AssistedInject constructor(
    @Assisted initialState: AppearanceState,
    private val observeAppearanceSettingsUseCase: ObserveAppearanceSettingsUseCase,
    private val setAppThemeModeUseCase: SetAppThemeModeUseCase,
    private val setScrollLongBookmarkUrlUseCase: SetScrollLongBookmarkUrlUseCase,
    private val setShowBookmarkUrlUseCase: SetShowBookmarkUrlUseCase,
    private val setOpenBookmarkAdjacentOnLargeScreenUseCase: SetOpenBookmarkAdjacentOnLargeScreenUseCase,
    private val setScrollLongFolderDescriptionUseCase: SetScrollLongFolderDescriptionUseCase,
    private val setShowFolderDescriptionUseCase: SetShowFolderDescriptionUseCase,
    private val setBookmarkFolderIconShapeUseCase: SetBookmarkFolderIconShapeUseCase,
    private val setBookmarkFolderIconColorUseCase: SetBookmarkFolderIconColorUseCase,
) : MavericksViewModel<AppearanceState>(initialState) {
    init {
        observeAppearanceSettings()
    }

    fun selectAppThemeMode(mode: String) {
        setState { copy(selectedThemeMode = mode) }
        viewModelScope.launch {
            setAppThemeModeUseCase(mode)
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

    fun setOpenBookmarkAdjacentOnLargeScreen(enabled: Boolean) {
        setState { copy(openBookmarkAdjacentOnLargeScreen = enabled) }
        viewModelScope.launch {
            setOpenBookmarkAdjacentOnLargeScreenUseCase(enabled)
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

    private fun observeAppearanceSettings() {
        viewModelScope.launch {
            observeAppearanceSettingsUseCase().collectLatest { appearanceSettings ->
                withState { current ->
                    val nextState = current.copy(
                        selectedThemeMode = appearanceSettings.selectedThemeMode,
                        showBookmarkUrl = appearanceSettings.showBookmarkUrl,
                        scrollLongBookmarkUrl = appearanceSettings.scrollLongBookmarkUrl,
                        openBookmarkAdjacentOnLargeScreen = appearanceSettings.openBookmarkAdjacentOnLargeScreen,
                        showFolderDescription = appearanceSettings.showFolderDescription,
                        scrollLongFolderDescription = appearanceSettings.scrollLongFolderDescription,
                        folderIconStyle = appearanceSettings.folderIconStyle,
                    )
                    if (nextState == current) return@withState
                    setState { nextState }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<AppearanceViewModel, AppearanceState> {
        override fun create(state: AppearanceState): AppearanceViewModel
    }

    companion object : MavericksViewModelFactory<AppearanceViewModel, AppearanceState> by hiltMavericksViewModelFactory()
}
