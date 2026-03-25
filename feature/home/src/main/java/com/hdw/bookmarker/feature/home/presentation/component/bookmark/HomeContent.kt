package com.hdw.bookmarker.feature.home.presentation.component.bookmark

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerYellow
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.home.presentation.component.appbar.HomeTopAppBar
import com.hdw.bookmarker.feature.home.presentation.component.bookmark.BookmarkDisplayContent
import com.hdw.bookmarker.feature.home.presentation.component.preview.BookmarkPreviewPane
import com.hdw.bookmarker.feature.home.presentation.component.preview.BookmarkPreviewPaneState
import com.hdw.bookmarker.feature.home.presentation.component.preview.rememberBookmarkPreviewPaneState
import com.hdw.bookmarker.feature.home.presentation.component.snapshot.BookmarkSnapshotBar
import com.hdw.bookmarker.feature.home.presentation.model.BookmarkDisplayType
import com.hdw.bookmarker.feature.home.presentation.model.HomeState

@Composable
internal fun HomeContent(
    state: HomeState,
    orderedSnapshotIds: List<SnapshotId>,
    selectedBookmarkId: SnapshotId?,
    pagerState: PagerState,
    defaultBrowserIcon: Drawable?,
    onSettingsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onBookmarkDisplayTypeToggle: () -> Unit,
    onDefaultBrowserPickerOpen: () -> Unit,
    onAddItemClick: () -> Unit,
    onImportClick: () -> Unit,
    onSnapshotClick: (SnapshotId) -> Unit,
    onSnapshotLongClick: (SnapshotId) -> Unit,
    onBookmarkClick: (String) -> Unit,
    onItemLongClick: (BookmarkItem, List<Int>) -> Unit,
    onBlankAreaLongClick: (List<Int>?) -> Unit,
    onSelectedFolderPathChange: (SnapshotId, List<Int>?) -> Unit,
    currentSnapshotTitle: String?,
    onSnapshotTitleClick: () -> Unit,
    onSnapshotExportClick: () -> Unit,
    showLargeScreenSidePreview: Boolean,
    previewPaneState: BookmarkPreviewPaneState,
    onPreviewOpenExternally: (String) -> Unit,
) {
    val currentSnapshotId = orderedSnapshotIds.getOrNull(pagerState.currentPage)
    val isCurrentInbox = currentSnapshotId != null && state.bookmarkSnapshots.isInbox(currentSnapshotId)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeTopAppBar(
                bookmarkDisplayType = state.bookmarkDisplayType,
                defaultBrowserIcon = defaultBrowserIcon,
                onSearchClick = onSearchClick,
                onBookmarkDisplayTypeClick = onBookmarkDisplayTypeToggle,
                onDefaultBrowserIconClick = onDefaultBrowserPickerOpen,
                onSettingsClick = onSettingsClick,
            )
        },
        floatingActionButton = {
            if (!showLargeScreenSidePreview && !isCurrentInbox) {
                AddItemFloatingActionButton(onClick = onAddItemClick)
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            BookmarkSnapshotBar(
                orderedSnapshotIds = orderedSnapshotIds,
                inboxSnapshotIds = state.bookmarkSnapshots.inboxIds,
                bookmarkColors = state.bookmarkColors,
                selectedBookmarkId = selectedBookmarkId,
                onAddClick = onImportClick,
                onSnapshotClick = onSnapshotClick,
                onSnapshotLongClick = onSnapshotLongClick,
            )

            if (orderedSnapshotIds.isEmpty()) {
                NoBookmarkItem(
                    modifier = Modifier.weight(1f),
                    onImportClick = onImportClick,
                )
            } else if (showLargeScreenSidePreview) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.42f)
                            .background(MaterialTheme.colorScheme.background),
                    ) {
                        BookmarkDocumentsPager(
                            state = state,
                            orderedSnapshotIds = orderedSnapshotIds,
                            pagerState = pagerState,
                            onImportClick = onImportClick,
                            onBookmarkClick = onBookmarkClick,
                            onItemLongClick = onItemLongClick,
                            onBlankAreaLongClick = onBlankAreaLongClick,
                            onSelectedFolderPathChange = onSelectedFolderPathChange,
                            currentSnapshotTitle = currentSnapshotTitle,
                            onSnapshotTitleClick = onSnapshotTitleClick,
                            onSnapshotExportClick = onSnapshotExportClick,
                            modifier = Modifier.fillMaxSize(),
                        )
                        if (!isCurrentInbox) {
                            AddItemFloatingActionButton(
                                onClick = onAddItemClick,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp),
                            )
                        }
                    }
                    VerticalDivider(modifier = Modifier.fillMaxHeight())
                    LargeScreenBookmarkPreviewPane(
                        previewPaneState = previewPaneState,
                        onPreviewOpenExternally = onPreviewOpenExternally,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.58f),
                    )
                }
            } else {
                BookmarkDocumentsPager(
                    state = state,
                    orderedSnapshotIds = orderedSnapshotIds,
                    pagerState = pagerState,
                    onImportClick = onImportClick,
                    onBookmarkClick = onBookmarkClick,
                    onItemLongClick = onItemLongClick,
                    onBlankAreaLongClick = onBlankAreaLongClick,
                    onSelectedFolderPathChange = onSelectedFolderPathChange,
                    currentSnapshotTitle = currentSnapshotTitle,
                    onSnapshotTitleClick = onSnapshotTitleClick,
                    onSnapshotExportClick = onSnapshotExportClick,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun AddItemFloatingActionButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = BookMarkerYellow,
        contentColor = Color.Black,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.add_bookmark_or_folder),
        )
    }
}

@Composable
private fun BookmarkDocumentsPager(
    state: HomeState,
    orderedSnapshotIds: List<SnapshotId>,
    pagerState: PagerState,
    onImportClick: () -> Unit,
    onBookmarkClick: (String) -> Unit,
    onItemLongClick: (BookmarkItem, List<Int>) -> Unit,
    onBlankAreaLongClick: (List<Int>?) -> Unit,
    onSelectedFolderPathChange: (SnapshotId, List<Int>?) -> Unit,
    currentSnapshotTitle: String?,
    onSnapshotTitleClick: () -> Unit,
    onSnapshotExportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier,
    ) { page ->
        val snapshotId = orderedSnapshotIds[page]
        val bookmarkDocument = state.bookmarkSnapshots[snapshotId]
        val snapshotFolderPath = state.selectedFolderPaths.pathOf(snapshotId)
        if (bookmarkDocument != null) {
            BookmarkDisplayContent(
                modifier = Modifier.fillMaxSize(),
                bookmarkDocument = bookmarkDocument,
                isInboxSnapshot = state.bookmarkSnapshots.isInbox(snapshotId),
                displayType = state.bookmarkDisplayType,
                scrollLongSecondaryInfo = state.scrollLongSecondaryInfo,
                secondaryDisplayType = state.secondaryDisplayType,
                showFolderDescription = state.showFolderDescription,
                scrollLongFolderDescription = state.scrollLongFolderDescription,
                folderIconStyle = state.folderIconStyle,
                onBookmarkClick = onBookmarkClick,
                onItemLongClick = onItemLongClick,
                onBlankAreaLongClick = onBlankAreaLongClick,
                onSelectedFolderPathChange = { path -> onSelectedFolderPathChange(snapshotId, path) },
                selectedFolderPath = snapshotFolderPath,
                snapshotTitle = currentSnapshotTitle,
                onSnapshotTitleClick = if (state.bookmarkSnapshots.isInbox(snapshotId)) null else onSnapshotTitleClick,
                onSnapshotExportClick = if (state.bookmarkSnapshots.isInbox(
                        snapshotId,
                    )
                ) {
                    null
                } else {
                    onSnapshotExportClick
                },
            )
        } else {
            NoBookmarkItem(
                modifier = Modifier.fillMaxSize(),
                onImportClick = onImportClick,
            )
        }
    }
}

@Composable
private fun LargeScreenBookmarkPreviewPane(
    previewPaneState: BookmarkPreviewPaneState,
    onPreviewOpenExternally: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BookmarkPreviewPane(
        url = previewPaneState.currentUrl,
        refreshToken = previewPaneState.refreshToken,
        onOpenExternally = onPreviewOpenExternally,
        onClose = previewPaneState::clear,
        onRefresh = previewPaneState::refresh,
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
internal fun HomeContentPreview() {
    val pagerState = rememberPagerState(pageCount = { 0 })
    HomeContent(
        state = HomeState(
            bookmarkDisplayType = BookmarkDisplayType.LIST,
        ),
        orderedSnapshotIds = emptyList(),
        selectedBookmarkId = null,
        pagerState = pagerState,
        defaultBrowserIcon = null,
        onSettingsClick = {},
        onSearchClick = {},
        onBookmarkDisplayTypeToggle = {},
        onDefaultBrowserPickerOpen = {},
        onAddItemClick = {},
        onImportClick = {},
        onSnapshotClick = {},
        onSnapshotLongClick = {},
        onBookmarkClick = {},
        onItemLongClick = { _, _ -> },
        onBlankAreaLongClick = {},
        onSelectedFolderPathChange = { _, _ -> },
        currentSnapshotTitle = "Preview",
        onSnapshotTitleClick = {},
        onSnapshotExportClick = {},
        showLargeScreenSidePreview = false,
        previewPaneState = rememberBookmarkPreviewPaneState(),
        onPreviewOpenExternally = {},
    )
}
