package com.hdw.bookmarker.feature.home.contract

import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconShape

data class HomeState(
    val installedBrowsers: List<BrowserInfo> = emptyList(),
    val orderedSnapshotIds: List<String> = emptyList(),
    val bookmarkDocuments: Map<String, BookmarkDocument> = emptyMap(),
    val bookmarkColors: Map<String, Long> = emptyMap(),
    val selectedBookmarkId: String? = null,
    val selectedFolderPaths: SnapshotFolderPathState = SnapshotFolderPathState(),
    val defaultBrowserPackage: String? = null,
    val bookmarkDisplayType: BookmarkDisplayType = BookmarkDisplayType.LIST,
    val folderIconShape: BookmarkFolderIconShape = BookmarkFolderIconShape.FILLED,
    val folderIconColor: BookmarkFolderIconColor = BookmarkFolderIconColor.DEFAULT,
    val isImporting: Boolean = false,
)

enum class BookmarkDisplayType {
    LIST,
    ICON,
}

data class SnapshotFolderPathState(private val pathsBySnapshotId: Map<String, List<Int>> = emptyMap()) {
    fun pathOf(snapshotId: String?): List<Int>? = snapshotId?.let(pathsBySnapshotId::get)

    fun update(snapshotId: String, path: List<Int>?): SnapshotFolderPathState {
        val updated = pathsBySnapshotId.toMutableMap()
        if (path.isNullOrEmpty()) {
            updated.remove(snapshotId)
        } else {
            updated[snapshotId] = path
        }
        return copy(pathsBySnapshotId = updated)
    }

    fun remove(snapshotId: String): SnapshotFolderPathState = copy(
        pathsBySnapshotId = pathsBySnapshotId - snapshotId,
    )

    fun retain(validSnapshotIds: Collection<String>): SnapshotFolderPathState = copy(
        pathsBySnapshotId = pathsBySnapshotId.filterKeys { it in validSnapshotIds },
    )
}
