package com.hdw.bookmarker.feature.home.ui
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerYellow
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.home.contract.HomeState
import com.hdw.bookmarker.feature.home.ui.appbar.HomeTopAppBar
import com.hdw.bookmarker.feature.home.ui.bookmarkdisplay.BookmarkDisplayContent
import com.hdw.bookmarker.feature.home.ui.preview.BookmarkPreviewPane
import com.hdw.bookmarker.feature.home.ui.preview.BookmarkPreviewPaneState
import kotlinx.coroutines.launch

@Composable
internal fun HomeContent(
    state: HomeState,
    orderedSnapshotIds: List<String>,
    selectedBookmarkId: String?,
    pagerState: PagerState,
    isBrowserEditMode: Boolean,
    defaultBrowserIcon: Drawable?,
    onSettingsClick: () -> Unit,
    onBookmarkDisplayTypeToggle: () -> Unit,
    onDefaultBrowserPickerOpen: () -> Unit,
    onEditLabelClick: () -> Unit,
    onEditModeDoneClick: () -> Unit,
    onAddItemClick: () -> Unit,
    onImportClick: () -> Unit,
    onEnterEditMode: () -> Unit,
    onDeleteSnapshotRequest: (String) -> Unit,
    onBookmarkClick: (String) -> Unit,
    onItemLongClick: (BookmarkItem, List<Int>) -> Unit,
    onSelectedFolderPathChange: (String, List<Int>?) -> Unit,
    currentSnapshotTitle: String?,
    onSnapshotTitleClick: () -> Unit,
    onSnapshotExportClick: () -> Unit,
    showLargeScreenSidePreview: Boolean,
    previewPaneState: BookmarkPreviewPaneState,
    onPreviewOpenExternally: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeTopAppBar(
                isEditMode = isBrowserEditMode,
                bookmarkDisplayType = state.bookmarkDisplayType,
                defaultBrowserIcon = defaultBrowserIcon,
                onBookmarkDisplayTypeClick = onBookmarkDisplayTypeToggle,
                onDefaultBrowserIconClick = onDefaultBrowserPickerOpen,
                onSettingsClick = onSettingsClick,
                onEditLabelClick = onEditLabelClick,
                onEditModeDoneClick = onEditModeDoneClick,
            )
        },
        floatingActionButton = {
            if (!showLargeScreenSidePreview) {
                AddItemFloatingActionButton(onClick = onAddItemClick)
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            BookmarkSnapshotTabBar(
                orderedSnapshotIds = orderedSnapshotIds,
                bookmarkColors = state.bookmarkColors,
                selectedBookmarkId = selectedBookmarkId,
                isEditMode = isBrowserEditMode,
                onAddClick = onImportClick,
                onSnapshotClick = { snapshotId ->
                    val targetPage = orderedSnapshotIds.indexOf(snapshotId)
                    if (targetPage >= 0 && targetPage != pagerState.currentPage) {
                        scope.launch {
                            pagerState.animateScrollToPage(targetPage)
                        }
                    }
                },
                onEnterEditMode = onEnterEditMode,
                onDeleteRequest = onDeleteSnapshotRequest,
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
                            onSelectedFolderPathChange = onSelectedFolderPathChange,
                            currentSnapshotTitle = currentSnapshotTitle,
                            onSnapshotTitleClick = onSnapshotTitleClick,
                            onSnapshotExportClick = onSnapshotExportClick,
                            modifier = Modifier.fillMaxSize(),
                        )
                        AddItemFloatingActionButton(
                            onClick = onAddItemClick,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                        )
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
private fun AddItemFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
    orderedSnapshotIds: List<String>,
    pagerState: PagerState,
    onImportClick: () -> Unit,
    onBookmarkClick: (String) -> Unit,
    onItemLongClick: (BookmarkItem, List<Int>) -> Unit,
    onSelectedFolderPathChange: (String, List<Int>?) -> Unit,
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
        val bookmarkDocument = state.bookmarkDocuments[snapshotId]
        val snapshotFolderPath = state.selectedFolderPaths.pathOf(snapshotId)
        if (bookmarkDocument != null) {
            BookmarkDisplayContent(
                modifier = Modifier.fillMaxSize(),
                bookmarkDocument = bookmarkDocument,
                displayType = state.bookmarkDisplayType,
                scrollLongBookmarkUrl = state.scrollLongBookmarkUrl,
                showBookmarkUrl = state.showBookmarkUrl,
                showFolderDescription = state.showFolderDescription,
                scrollLongFolderDescription = state.scrollLongFolderDescription,
                folderIconStyle = state.folderIconStyle,
                onBookmarkClick = onBookmarkClick,
                onItemLongClick = onItemLongClick,
                onSelectedFolderPathChange = { path -> onSelectedFolderPathChange(snapshotId, path) },
                selectedFolderPath = snapshotFolderPath,
                snapshotTitle = currentSnapshotTitle,
                onSnapshotTitleClick = onSnapshotTitleClick,
                onSnapshotExportClick = onSnapshotExportClick,
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
