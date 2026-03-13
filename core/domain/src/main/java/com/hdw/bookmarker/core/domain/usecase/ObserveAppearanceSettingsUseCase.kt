package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.model.settings.AppearanceSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ObserveAppearanceSettingsUseCase @Inject constructor(
    private val getAppThemeModeUseCase: GetAppThemeModeUseCase,
    private val getShowBookmarkUrlUseCase: GetShowBookmarkUrlUseCase,
    private val getScrollLongBookmarkUrlUseCase: GetScrollLongBookmarkUrlUseCase,
    private val getOpenBookmarkSidePreviewOnLargeScreenUseCase: GetOpenBookmarkSidePreviewOnLargeScreenUseCase,
    private val getShowFolderDescriptionUseCase: GetShowFolderDescriptionUseCase,
    private val getScrollLongFolderDescriptionUseCase: GetScrollLongFolderDescriptionUseCase,
    private val getBookmarkFolderIconStyleUseCase: GetBookmarkFolderIconStyleUseCase,
) {
    operator fun invoke(): Flow<AppearanceSettings> {
        val bookmarkAppearance = combine(
            getShowBookmarkUrlUseCase(),
            getScrollLongBookmarkUrlUseCase(),
            getOpenBookmarkSidePreviewOnLargeScreenUseCase(),
        ) { showBookmarkUrl, scrollLongBookmarkUrl, openBookmarkSidePreviewOnLargeScreen ->
            Triple(showBookmarkUrl, scrollLongBookmarkUrl, openBookmarkSidePreviewOnLargeScreen)
        }

        val folderAppearance = combine(
            getShowFolderDescriptionUseCase(),
            getScrollLongFolderDescriptionUseCase(),
            getBookmarkFolderIconStyleUseCase(),
        ) { showFolderDescription, scrollLongFolderDescription, folderIconStyle ->
            Triple(showFolderDescription, scrollLongFolderDescription, folderIconStyle)
        }

        return combine(
            getAppThemeModeUseCase(),
            bookmarkAppearance,
            folderAppearance,
        ) { selectedThemeMode, bookmarkAppearanceState, folderAppearanceState ->
            AppearanceSettings(
                selectedThemeMode = selectedThemeMode,
                showBookmarkUrl = bookmarkAppearanceState.first,
                scrollLongBookmarkUrl = bookmarkAppearanceState.second,
                openBookmarkSidePreviewOnLargeScreen = bookmarkAppearanceState.third,
                showFolderDescription = folderAppearanceState.first,
                scrollLongFolderDescription = folderAppearanceState.second,
                folderIconStyle = folderAppearanceState.third,
            )
        }
    }
}
