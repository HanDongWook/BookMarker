package com.hdw.bookmarker.feature.home.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.home.contract.AddBookmarkItemRequest
import com.hdw.bookmarker.feature.home.contract.BookmarkDisplayType
import com.hdw.bookmarker.feature.home.contract.HomeState
import com.hdw.bookmarker.feature.home.contract.QuickSaveBookmarkSeed
import com.hdw.bookmarker.feature.home.contract.UpdateBookmarkItemRequest
import com.hdw.bookmarker.feature.home.search.model.BookmarkSearchItemType
import com.hdw.bookmarker.feature.home.search.model.BookmarkSearchResult
import com.hdw.bookmarker.feature.home.ui.dialog.HomeDialogHost
import com.hdw.bookmarker.feature.home.ui.export.BookmarkExportAction
import com.hdw.bookmarker.feature.home.ui.search.BookmarkSearchDialog
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    state: HomeState,
    enableLargeScreenSidePreview: Boolean,
    pendingQuickSaveRequestToken: Long?,
    pendingQuickSaveRequest: QuickSaveBookmarkSeed?,
    onQuickSaveRequestHandled: () -> Unit,
    onSettingsClick: () -> Unit,
    onOpenBookmark: (String, String?) -> Boolean,
    onOpenBookmarkImportGuide: () -> Unit,
    onSnapshotSelected: (SnapshotId) -> Unit,
    onSelectedFolderPathChange: (SnapshotId, List<Int>?) -> Unit,
    onBookmarkColorSelected: (SnapshotId, Long) -> Unit,
    onDefaultBrowserSelected: (String) -> Unit,
    onDeleteBookmarkSnapshot: (SnapshotId) -> Unit,
    onBookmarkDisplayTypeToggle: () -> Unit,
    onRenameBookmarkSnapshot: (SnapshotId, String) -> Unit,
    onUpdateBookmarkItem: (UpdateBookmarkItemRequest) -> Unit,
    onDeleteBookmarkItem: (List<Int>) -> Unit,
    onAddBookmarkItem: (AddBookmarkItemRequest) -> Unit,
    onAddEmptyBookmarkSnapshot: () -> Unit,
    onBookmarkExportRequest: (BookmarkExportAction, com.hdw.bookmarker.core.model.bookmark.BookmarkDocument) -> Unit,
) {
    val uiState = rememberHomeScreenUiState()
    var showAddItemTypeDialog by uiState.showAddItemTypeDialog
    var showImportOptionDialog by uiState.showImportOptionDialog
    var isBrowserEditMode by uiState.isBrowserEditMode
    var showDefaultBrowserDialog by uiState.showDefaultBrowserDialog
    var showColorPickerDialog by uiState.showColorPickerDialog
    var showExportBookmarkMethodDialog by uiState.showExportBookmarkMethodDialog
    var showSearchDialog by uiState.showSearchDialog
    var searchQuery by uiState.searchQuery
    var showAddBookmarkDialog by uiState.showAddBookmarkDialog
    var showAddFolderDialog by uiState.showAddFolderDialog
    var pendingFolderTitle by uiState.pendingFolderTitle
    var pendingFolderDescription by uiState.pendingFolderDescription
    var pendingBookmarkTitle by uiState.pendingBookmarkTitle
    var pendingBookmarkUrl by uiState.pendingBookmarkUrl
    var pendingBookmarkDescription by uiState.pendingBookmarkDescription
    var pendingBookmarkTags by uiState.pendingBookmarkTags
    var addBookmarkToInbox by uiState.addBookmarkToInbox
    var pendingDeleteSnapshotId by uiState.pendingDeleteSnapshotId
    var pendingRenameSnapshotId by uiState.pendingRenameSnapshotId
    var pendingSnapshotTitle by uiState.pendingSnapshotTitle
    var pendingBookmarkExportAction by uiState.pendingBookmarkExportAction
    var pendingEditBookmarkItem by uiState.pendingEditBookmarkItem
    var pendingEditBookmarkItemPath by uiState.pendingEditBookmarkItemPath
    var pendingEditBookmarkTitle by uiState.pendingEditBookmarkTitle
    var pendingEditBookmarkUrl by uiState.pendingEditBookmarkUrl
    var pendingEditBookmarkTags by uiState.pendingEditBookmarkTags
    var pendingEditBookmarkDescription by uiState.pendingEditBookmarkDescription
    val orderedSnapshotIds = remember(state.bookmarkSnapshots) {
        state.bookmarkSnapshots.orderedIds.filter { id -> !state.bookmarkSnapshots.isEmptyInbox(id) }
    }
    val context = LocalContext.current

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { orderedSnapshotIds.size },
    )
    val scope = rememberCoroutineScope()
    val selectedBookmarkId = state.selectedBookmarkId
        ?.takeIf { id -> orderedSnapshotIds.contains(id) }
        ?: orderedSnapshotIds.getOrNull(pagerState.currentPage)
        ?: orderedSnapshotIds.firstOrNull()
    val selectedFolderPath = state.selectedFolderPaths.pathOf(selectedBookmarkId)
    val defaultSnapshotTitlePrefix = stringResource(R.string.default_snapshot_title_prefix)
    val snapshotTitles = remember(orderedSnapshotIds, state.bookmarkSnapshots, defaultSnapshotTitlePrefix) {
        orderedSnapshotIds.mapIndexed { index, snapshotId ->
            val defaultTitle = "$defaultSnapshotTitlePrefix${index + 1}"
            snapshotId to (
                state.bookmarkSnapshots[snapshotId]
                    ?.title
                    ?.trim()
                    ?.takeIf { it.isNotBlank() }
                    ?: defaultTitle
                )
        }.toMap()
    }

    val defaultBrowserIcon = state.installedBrowsers
        .firstOrNull { it.packageName == state.defaultBrowserPackage }
        ?.icon
    val selectedBookmarkDocument = selectedBookmarkId
        ?.let { state.bookmarkSnapshots[it] }
    val dismissSearchDialog = {
        showSearchDialog = false
        searchQuery = ""
    }

    LaunchedEffect(pendingQuickSaveRequestToken) {
        val quickSaveRequest = pendingQuickSaveRequest ?: return@LaunchedEffect
        if (pendingQuickSaveRequestToken == null) return@LaunchedEffect
        pendingBookmarkTitle = quickSaveRequest.title
        pendingBookmarkUrl = quickSaveRequest.url
        pendingBookmarkDescription = quickSaveRequest.description
        pendingBookmarkTags = quickSaveRequest.tags.joinToString(separator = ", ")
        addBookmarkToInbox = true
        showAddBookmarkDialog = true
        onQuickSaveRequestHandled()
    }

    val onSearchResultClick: (BookmarkSearchResult) -> Unit = { result ->
        val targetFolderPath = when (result.itemType) {
            BookmarkSearchItemType.FOLDER -> result.itemPath
            BookmarkSearchItemType.BOOKMARK -> result.revealFolderPath
        }
        onSnapshotSelected(result.snapshotId)
        onSelectedFolderPathChange(result.snapshotId, targetFolderPath)
        dismissSearchDialog()
        val targetPage = orderedSnapshotIds.indexOf(result.snapshotId)
        if (targetPage >= 0 && targetPage != pagerState.currentPage) {
            scope.launch {
                pagerState.animateScrollToPage(targetPage)
            }
        }
    }

    LaunchedEffect(pagerState, orderedSnapshotIds) {
        if (orderedSnapshotIds.isEmpty()) return@LaunchedEffect
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                orderedSnapshotIds.getOrNull(page)?.let(onSnapshotSelected)
            }
    }

    LaunchedEffect(enableLargeScreenSidePreview) {
        if (!enableLargeScreenSidePreview) {
            uiState.previewPaneState.clear()
        }
    }

    LaunchedEffect(pendingBookmarkExportAction, selectedBookmarkDocument) {
        val exportAction = pendingBookmarkExportAction ?: return@LaunchedEffect
        selectedBookmarkDocument?.let { document ->
            onBookmarkExportRequest(exportAction, document)
        }
        pendingBookmarkExportAction = null
    }

    HomeScreenBackHandler(uiState = uiState)
    HomeDialogHost(
        state = state,
        uiState = uiState,
        selectedBookmarkId = selectedBookmarkId,
        selectedFolderPath = selectedFolderPath,
        onOpenBookmarkImportGuide = onOpenBookmarkImportGuide,
        onAddEmptyBookmarkSnapshot = onAddEmptyBookmarkSnapshot,
        onDeleteBookmarkSnapshot = onDeleteBookmarkSnapshot,
        onRenameBookmarkSnapshot = onRenameBookmarkSnapshot,
        onUpdateBookmarkItem = onUpdateBookmarkItem,
        onDeleteBookmarkItem = onDeleteBookmarkItem,
        onAddBookmarkItem = onAddBookmarkItem,
        onDefaultBrowserSelected = onDefaultBrowserSelected,
        onBookmarkColorSelected = onBookmarkColorSelected,
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isImporting) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                trackColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
            )
        } else {
            HomeContent(
                state = state,
                orderedSnapshotIds = orderedSnapshotIds,
                selectedBookmarkId = selectedBookmarkId,
                pagerState = pagerState,
                isBrowserEditMode = isBrowserEditMode,
                defaultBrowserIcon = defaultBrowserIcon,
                onSettingsClick = onSettingsClick,
                onSearchClick = { showSearchDialog = true },
                onBookmarkDisplayTypeToggle = onBookmarkDisplayTypeToggle,
                onDefaultBrowserPickerOpen = {
                    if (state.installedBrowsers.isNotEmpty()) {
                        showDefaultBrowserDialog = true
                    }
                },
                onEditLabelClick = { showColorPickerDialog = true },
                onEditModeDoneClick = { isBrowserEditMode = false },
                onAddItemClick = { showAddItemTypeDialog = true },
                onImportClick = { showImportOptionDialog = true },
                onEnterEditMode = { isBrowserEditMode = true },
                onDeleteSnapshotRequest = { snapshotId -> pendingDeleteSnapshotId = snapshotId },
                onBookmarkClick = { url ->
                    if (enableLargeScreenSidePreview) {
                        uiState.previewPaneState.open(url)
                    } else if (!onOpenBookmark(url, state.defaultBrowserPackage)) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.open_bookmark_failed),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                },
                onItemLongClick = { item, path ->
                    pendingEditBookmarkItem = item
                    pendingEditBookmarkItemPath = path
                    pendingEditBookmarkTitle = when (item) {
                        is BookmarkItem.Folder -> item.title
                        is BookmarkItem.Bookmark -> item.title
                    }
                    pendingEditBookmarkUrl = (item as? BookmarkItem.Bookmark)?.url.orEmpty()
                    pendingEditBookmarkTags = (item as? BookmarkItem.Bookmark)?.tags
                        ?.joinToString(separator = ", ")
                        .orEmpty()
                    pendingEditBookmarkDescription = when (item) {
                        is BookmarkItem.Bookmark -> item.description.orEmpty()
                        is BookmarkItem.Folder -> item.description.orEmpty()
                    }
                    uiState.showBookmarkItemActionDialog.value = true
                },
                onSelectedFolderPathChange = onSelectedFolderPathChange,
                currentSnapshotTitle = selectedBookmarkId?.let(snapshotTitles::get),
                onSnapshotTitleClick = {
                    if (selectedBookmarkId != null) {
                        pendingRenameSnapshotId = selectedBookmarkId
                        pendingSnapshotTitle = snapshotTitles[selectedBookmarkId].orEmpty()
                    }
                },
                onSnapshotExportClick = { showExportBookmarkMethodDialog = true },
                previewPaneState = uiState.previewPaneState,
                showLargeScreenSidePreview = enableLargeScreenSidePreview,
                onPreviewOpenExternally = { url ->
                    if (!onOpenBookmark(url, state.defaultBrowserPackage)) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.open_bookmark_failed),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                },
            )
            if (showSearchDialog) {
                BookmarkSearchDialog(
                    query = searchQuery,
                    library = state.bookmarkSnapshots,
                    snapshotTitles = snapshotTitles,
                    folderIconStyle = state.folderIconStyle,
                    onQueryChange = { searchQuery = it },
                    onDismissRequest = dismissSearchDialog,
                    onResultClick = onSearchResultClick,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
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
        onAddEmptyBookmarkSnapshot = {},
        onBookmarkExportRequest = { _, _ -> },
    )
}
