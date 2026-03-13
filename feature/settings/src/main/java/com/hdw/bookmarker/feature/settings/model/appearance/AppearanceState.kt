package com.hdw.bookmarker.feature.settings.model.appearance

import com.airbnb.mvrx.MavericksState
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle

data class AppearanceState(
    val selectedThemeMode: String? = null,
    val showBookmarkUrl: Boolean = true,
    val scrollLongBookmarkUrl: Boolean = true,
    val showFolderDescription: Boolean = true,
    val scrollLongFolderDescription: Boolean = true,
    val folderIconStyle: BookmarkFolderIconStyle = BookmarkFolderIconStyle(),
) : MavericksState
