package com.hdw.bookmarker.feature.home.search.model

import com.hdw.bookmarker.core.model.bookmark.SnapshotId

enum class BookmarkSearchItemType {
    FOLDER,
    BOOKMARK,
}

data class BookmarkSearchResult(
    val snapshotId: SnapshotId,
    val snapshotTitle: String,
    val itemPath: List<Int>,
    val revealFolderPath: List<Int>?,
    val itemType: BookmarkSearchItemType,
    val title: String,
    val secondaryText: String?,
    val breadcrumb: String,
    val bookmarkUrl: String? = null,
    val bookmarkIconUri: String? = null,
)
