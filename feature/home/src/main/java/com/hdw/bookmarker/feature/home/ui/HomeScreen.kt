package com.hdw.bookmarker.feature.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.home.contract.AddBookmarkItemRequest
import com.hdw.bookmarker.feature.home.contract.BookmarkDisplayType
import com.hdw.bookmarker.feature.home.contract.HomeState
import com.hdw.bookmarker.feature.home.contract.UpdateBookmarkItemRequest
import com.hdw.bookmarker.feature.home.search.model.BookmarkSearchItemType
import com.hdw.bookmarker.feature.home.search.model.BookmarkSearchResult
import com.hdw.bookmarker.feature.home.ui.dialog.HomeDialogHost
import com.hdw.bookmarker.feature.home.ui.search.BookmarkSearchDialog
import com.hdw.bookmarker.feature.home.ui.preview.BookmarkPreviewPaneState
import com.hdw.bookmarker.feature.home.ui.preview.rememberBookmarkPreviewPaneState
import com.hdw.bookmarker.feature.home.ui.export.BookmarkExportAction
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun HomeScreen(
    state: HomeState,
    enableLargeScreenSidePreview: Boolean,
    onSettingsClick: () -> Unit,
    onOpenBookmark: (String, String?) -> Boolean,
    onOpenBookmarkImportGuide: () -> Unit,
    onSnapshotSelected: (String) -> Unit,
    onSelectedFolderPathChange: (String, List<Int>?) -> Unit,
    onBookmarkColorSelected: (String, Long) -> Unit,
    onDefaultBrowserSelected: (String) -> Unit,
    onDeleteBookmarkSnapshot: (String) -> Unit,
    onBookmarkDisplayTypeToggle: () -> Unit,
    onAddBookmarkItem: (AddBookmarkItemRequest) -> Unit,
    onRenameBookmarkSnapshot: (String, String) -> Unit,
    onDeleteBookmarkItem: (List<Int>) -> Unit,
    onUpdateBookmarkItem: (UpdateBookmarkItemRequest) -> Unit,
    onAddEmptyBookmarkSnapshot: () -> Unit,
    onBookmarkExportRequest: (BookmarkExportAction, BookmarkDocument) -> Unit,
) {
    val uiState = rememberHomeScreenUiState()
    var showImportOptionDialog by uiState.showImportOptionDialog
    var isBrowserEditMode by uiState.isBrowserEditMode
    var showDefaultBrowserDialog by uiState.showDefaultBrowserDialog
    var showColorPickerDialog by uiState.showColorPickerDialog
    var showAddItemTypeDialog by uiState.showAddItemTypeDialog
    var showExportBookmarkMethodDialog by uiState.showExportBookmarkMethodDialog
    var pendingBookmarkExportAction by uiState.pendingBookmarkExportAction
    var showAddFolderDialog by uiState.showAddFolderDialog
    var showAddBookmarkDialog by uiState.showAddBookmarkDialog
    var pendingFolderTitle by uiState.pendingFolderTitle
    var pendingFolderDescription by uiState.pendingFolderDescription
    var pendingBookmarkTitle by uiState.pendingBookmarkTitle
    var pendingBookmarkUrl by uiState.pendingBookmarkUrl
    var pendingDeleteSnapshotId by uiState.pendingDeleteSnapshotId
    var pendingRenameSnapshotId by uiState.pendingRenameSnapshotId
    var pendingSnapshotTitle by uiState.pendingSnapshotTitle
    var showSearchDialog by uiState.showSearchDialog
    var searchQuery by uiState.searchQuery
    var pendingEditBookmarkItemPath by uiState.pendingEditBookmarkItemPath
    var pendingDeleteBookmarkItemPath by uiState.pendingDeleteBookmarkItemPath
    var pendingEditBookmarkItem by uiState.pendingEditBookmarkItem
    var pendingEditBookmarkTitle by uiState.pendingEditBookmarkTitle
    var pendingEditBookmarkUrl by uiState.pendingEditBookmarkUrl
    var pendingEditBookmarkDescription by uiState.pendingEditBookmarkDescription
    val orderedSnapshotIds = state.orderedSnapshotIds
    val context = LocalContext.current
    val resources = LocalResources.current

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
    val defaultSnapshotTitlePrefix = resources.getString(R.string.default_snapshot_title_prefix)
    val snapshotTitles = remember(orderedSnapshotIds, state.bookmarkDocuments, defaultSnapshotTitlePrefix) {
        orderedSnapshotIds.mapIndexed { index, snapshotId ->
            val defaultTitle = "$defaultSnapshotTitlePrefix${index + 1}"
            snapshotId to (
                state.bookmarkDocuments[snapshotId]
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
        ?.let { state.bookmarkDocuments[it] }
    val dismissSearchDialog = {
        showSearchDialog = false
        searchQuery = ""
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
                        context.showShortToast(resources.getString(R.string.open_bookmark_failed))
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
                    pendingEditBookmarkDescription = (item as? BookmarkItem.Folder)?.description.orEmpty()
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
                        context.showShortToast(resources.getString(R.string.open_bookmark_failed))
                    }
                },
            )
            if (showSearchDialog) {
                BookmarkSearchDialog(
                    query = searchQuery,
                    orderedSnapshotIds = orderedSnapshotIds,
                    bookmarkDocuments = state.bookmarkDocuments,
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

internal class HomeScreenUiState(
    val showImportOptionDialog: MutableState<Boolean>,
    val isBrowserEditMode: MutableState<Boolean>,
    val showDefaultBrowserDialog: MutableState<Boolean>,
    val showColorPickerDialog: MutableState<Boolean>,
    val showAddItemTypeDialog: MutableState<Boolean>,
    val showExportBookmarkMethodDialog: MutableState<Boolean>,
    val pendingBookmarkExportAction: MutableState<BookmarkExportAction?>,
    val showAddFolderDialog: MutableState<Boolean>,
    val showAddBookmarkDialog: MutableState<Boolean>,
    val pendingFolderTitle: MutableState<String>,
    val pendingFolderDescription: MutableState<String>,
    val pendingBookmarkTitle: MutableState<String>,
    val pendingBookmarkUrl: MutableState<String>,
    val pendingDeleteSnapshotId: MutableState<String?>,
    val pendingRenameSnapshotId: MutableState<String?>,
    val pendingSnapshotTitle: MutableState<String>,
    val showSearchDialog: MutableState<Boolean>,
    val searchQuery: MutableState<String>,
    val pendingEditBookmarkItemPath: MutableState<List<Int>?>,
    val pendingDeleteBookmarkItemPath: MutableState<List<Int>?>,
    val pendingEditBookmarkItem: MutableState<BookmarkItem?>,
    val pendingEditBookmarkTitle: MutableState<String>,
    val pendingEditBookmarkUrl: MutableState<String>,
    val pendingEditBookmarkDescription: MutableState<String>,
    val previewPaneState: BookmarkPreviewPaneState,
)

@Composable
private fun rememberHomeScreenUiState(): HomeScreenUiState = HomeScreenUiState(
    showImportOptionDialog = rememberSaveable { mutableStateOf(false) },
    isBrowserEditMode = rememberSaveable { mutableStateOf(false) },
    showDefaultBrowserDialog = rememberSaveable { mutableStateOf(false) },
    showColorPickerDialog = rememberSaveable { mutableStateOf(false) },
    showAddItemTypeDialog = rememberSaveable { mutableStateOf(false) },
    showExportBookmarkMethodDialog = rememberSaveable { mutableStateOf(false) },
    pendingBookmarkExportAction = remember { mutableStateOf<BookmarkExportAction?>(null) },
    showAddFolderDialog = rememberSaveable { mutableStateOf(false) },
    showAddBookmarkDialog = rememberSaveable { mutableStateOf(false) },
    pendingFolderTitle = rememberSaveable { mutableStateOf("") },
    pendingFolderDescription = rememberSaveable { mutableStateOf("") },
    pendingBookmarkTitle = rememberSaveable { mutableStateOf("") },
    pendingBookmarkUrl = rememberSaveable { mutableStateOf("") },
    pendingDeleteSnapshotId = rememberSaveable { mutableStateOf<String?>(null) },
    pendingRenameSnapshotId = rememberSaveable { mutableStateOf<String?>(null) },
    pendingSnapshotTitle = rememberSaveable { mutableStateOf("") },
    showSearchDialog = rememberSaveable { mutableStateOf(false) },
    searchQuery = rememberSaveable { mutableStateOf("") },
    pendingEditBookmarkItemPath = remember { mutableStateOf<List<Int>?>(null) },
    pendingDeleteBookmarkItemPath = remember { mutableStateOf<List<Int>?>(null) },
    pendingEditBookmarkItem = remember { mutableStateOf<BookmarkItem?>(null) },
    pendingEditBookmarkTitle = rememberSaveable { mutableStateOf("") },
    pendingEditBookmarkUrl = rememberSaveable { mutableStateOf("") },
    pendingEditBookmarkDescription = rememberSaveable { mutableStateOf("") },
    previewPaneState = rememberBookmarkPreviewPaneState(),
)

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        state = HomeState(
            orderedSnapshotIds = emptyList(),
            bookmarkDisplayType = BookmarkDisplayType.LIST,
        ),
        enableLargeScreenSidePreview = false,
        onSettingsClick = {},
        onOpenBookmark = { _, _ -> true },
        onOpenBookmarkImportGuide = {},
        onSnapshotSelected = {},
        onSelectedFolderPathChange = { _, _ -> },
        onBookmarkColorSelected = { _, _ -> },
        onDefaultBrowserSelected = {},
        onDeleteBookmarkSnapshot = {},
        onBookmarkDisplayTypeToggle = {},
        onAddBookmarkItem = { _ -> },
        onRenameBookmarkSnapshot = { _, _ -> },
        onDeleteBookmarkItem = {},
        onUpdateBookmarkItem = { _ -> },
        onAddEmptyBookmarkSnapshot = {},
        onBookmarkExportRequest = { _, _ -> },
    )
}
