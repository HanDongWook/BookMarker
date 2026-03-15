package com.hdw.bookmarker.feature.home.presentation.model

import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.model.settings.BookmarkSecondaryDisplayType
import com.hdw.bookmarker.feature.home.domain.model.BookmarkSnapshots

data class HomeState(
    val installedBrowsers: List<BrowserInfo> = emptyList(),
    val bookmarkSnapshots: BookmarkSnapshots = BookmarkSnapshots(),
    val bookmarkColors: Map<SnapshotId, Long> = emptyMap(),
    val selectedBookmarkId: SnapshotId? = null,
    val selectedFolderPaths: SnapshotFolderPathState = SnapshotFolderPathState(),
    val defaultBrowserPackage: String? = null,
    val bookmarkDisplayType: BookmarkDisplayType = BookmarkDisplayType.LIST,
    val scrollLongSecondaryInfo: Boolean = true,
    val secondaryDisplayType: BookmarkSecondaryDisplayType = BookmarkSecondaryDisplayType.URL,
    val openBookmarkAdjacentOnLargeScreen: Boolean = false,
    val openBookmarkSidePreviewOnLargeScreen: Boolean = false,
    val showFolderDescription: Boolean = true,
    val scrollLongFolderDescription: Boolean = true,
    val folderIconStyle: BookmarkFolderIconStyle = BookmarkFolderIconStyle(),
    val isImporting: Boolean = false,
)

enum class BookmarkDisplayType {
    LIST,
    ICON,
}

data class SnapshotFolderPathState(private val pathsBySnapshotId: Map<SnapshotId, List<Int>> = emptyMap()) {
    fun pathOf(snapshotId: SnapshotId?): List<Int>? = snapshotId?.let(pathsBySnapshotId::get)

    fun update(snapshotId: SnapshotId, path: List<Int>?): SnapshotFolderPathState {
        val updated = pathsBySnapshotId.toMutableMap()
        if (path.isNullOrEmpty()) {
            updated.remove(snapshotId)
        } else {
            updated[snapshotId] = path
        }
        return copy(pathsBySnapshotId = updated)
    }

    fun remove(snapshotId: SnapshotId): SnapshotFolderPathState = copy(
        pathsBySnapshotId = pathsBySnapshotId - snapshotId,
    )

    fun retain(validSnapshotIds: Collection<SnapshotId>): SnapshotFolderPathState = copy(
        pathsBySnapshotId = pathsBySnapshotId.filterKeys { it in validSnapshotIds },
    )
}
