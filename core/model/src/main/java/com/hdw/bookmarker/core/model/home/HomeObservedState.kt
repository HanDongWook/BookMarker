package com.hdw.bookmarker.core.model.home

import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle

data class HomeObservedState(
    val orderedSnapshotIds: List<String>,
    val bookmarkDocuments: Map<String, BookmarkDocument>,
    val bookmarkColors: Map<String, Long>,
    val defaultBrowserPackage: String?,
    val bookmarkDisplayType: String?,
    val scrollLongBookmarkUrl: Boolean,
    val bookmarkSecondaryDisplayType: String?,
    val openBookmarkAdjacentOnLargeScreen: Boolean,
    val openBookmarkSidePreviewOnLargeScreen: Boolean,
    val showFolderDescription: Boolean,
    val scrollLongFolderDescription: Boolean,
    val folderIconStyle: BookmarkFolderIconStyle,
)
