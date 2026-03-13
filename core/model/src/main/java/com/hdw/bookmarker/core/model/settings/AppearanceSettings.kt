package com.hdw.bookmarker.core.model.settings

import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle

data class AppearanceSettings(
    val selectedThemeMode: String?,
    val showBookmarkUrl: Boolean,
    val scrollLongBookmarkUrl: Boolean,
    val openBookmarkAdjacentOnLargeScreen: Boolean,
    val showFolderDescription: Boolean,
    val scrollLongFolderDescription: Boolean,
    val folderIconStyle: BookmarkFolderIconStyle,
)
