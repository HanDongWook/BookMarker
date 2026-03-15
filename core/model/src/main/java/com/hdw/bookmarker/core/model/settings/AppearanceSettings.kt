package com.hdw.bookmarker.core.model.settings

import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle

data class AppearanceSettings(
    val selectedThemeMode: String?,
    val secondaryDisplayType: BookmarkSecondaryDisplayType,
    val scrollLongBookmarkUrl: Boolean,
    val openBookmarkSidePreviewOnLargeScreen: Boolean,
    val showFolderDescription: Boolean,
    val scrollLongFolderDescription: Boolean,
    val folderIconStyle: BookmarkFolderIconStyle,
)
