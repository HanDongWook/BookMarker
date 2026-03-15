package com.hdw.bookmarker.feature.settings.presentation.model.appearance

import com.airbnb.mvrx.MavericksState
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.model.settings.BookmarkSecondaryDisplayType

data class AppearanceState(
    val selectedThemeMode: String? = null,
    val secondaryDisplayType: BookmarkSecondaryDisplayType = BookmarkSecondaryDisplayType.URL,
    val scrollLongSecondaryInfo: Boolean = true,
    val openBookmarkSidePreviewOnLargeScreen: Boolean = false,
    val showFolderDescription: Boolean = true,
    val scrollLongFolderDescription: Boolean = true,
    val folderIconStyle: BookmarkFolderIconStyle = BookmarkFolderIconStyle(),
) : MavericksState
