package com.hdw.bookmarker.feature.home.ui

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.feature.home.contract.BookmarkDisplayType
import com.hdw.bookmarker.feature.home.contract.HomeState
import com.hdw.bookmarker.feature.home.ui.preview.rememberBookmarkPreviewPaneState

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    HomeContent(
        state = HomeState(
            orderedSnapshotIds = emptyList(),
            bookmarkDisplayType = BookmarkDisplayType.LIST,
            scrollLongBookmarkUrl = true,
            showBookmarkUrl = true,
            showFolderDescription = true,
            scrollLongFolderDescription = true,
        ),
        orderedSnapshotIds = emptyList(),
        selectedBookmarkId = null,
        pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { 0 },
        ),
        isBrowserEditMode = false,
        defaultBrowserIcon = null,
        onSettingsClick = {},
        onSearchClick = {},
        onBookmarkDisplayTypeToggle = {},
        onDefaultBrowserPickerOpen = {},
        onEditLabelClick = {},
        onEditModeDoneClick = {},
        onAddItemClick = {},
        onImportClick = {},
        onEnterEditMode = {},
        onDeleteSnapshotRequest = {},
        onBookmarkClick = {},
        onItemLongClick = { _, _ -> },
        onSelectedFolderPathChange = { _, _ -> },
        currentSnapshotTitle = "북마크1",
        onSnapshotTitleClick = {},
        onSnapshotExportClick = {},
        showLargeScreenSidePreview = false,
        previewPaneState = rememberBookmarkPreviewPaneState(),
        onPreviewOpenExternally = {},
    )
}
