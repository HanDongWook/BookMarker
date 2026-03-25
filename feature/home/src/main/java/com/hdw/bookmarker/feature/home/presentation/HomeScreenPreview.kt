package com.hdw.bookmarker.feature.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.feature.home.presentation.model.BookmarkDisplayType
import com.hdw.bookmarker.feature.home.presentation.model.HomeState

@Preview(showBackground = true)
@Composable
internal fun HomeScreenPreview() {
    HomeScreen(
        state = HomeState(
            bookmarkDisplayType = BookmarkDisplayType.LIST,
        ),
        enableLargeScreenSidePreview = false,
        pendingQuickSaveRequestToken = null,
        pendingQuickSaveRequest = null,
        onQuickSaveRequestHandled = {},
        onSettingsClick = {},
        onOpenBookmark = { _, _ -> true },
        onOpenBookmarkImportGuide = {},
        onSnapshotSelected = {},
        onSelectedFolderPathChange = { _, _ -> },
        onBookmarkColorSelected = { _, _ -> },
        onDefaultBrowserSelected = {},
        onDeleteBookmarkSnapshot = {},
        onBookmarkDisplayTypeToggle = {},
        onRenameBookmarkSnapshot = { _, _ -> },
        onUpdateBookmarkItem = {},
        onDeleteBookmarkItem = {},
        onAddBookmarkItem = {},
        onMoveBookmark = { _, _, _, _ -> },
        onAddEmptyBookmarkSnapshot = {},
        onBookmarkExportRequest = { _, _ -> },
    )
}
