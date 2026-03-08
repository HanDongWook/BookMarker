package com.hdw.bookmarker.feature.home

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.home.contract.BookmarkDisplayType
import com.hdw.bookmarker.feature.home.contract.HomeState
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun HomeScreen(
    state: HomeState,
    onSettingsClick: () -> Unit,
    onOpenBookmark: (String, String?) -> Boolean,
    onOpenBookmarkImportGuide: () -> Unit,
    onSnapshotSelected: (String) -> Unit,
    onSelectedFolderPathChange: (String, List<Int>?) -> Unit,
    onBookmarkColorSelected: (String, Long) -> Unit,
    onDefaultBrowserSelected: (String) -> Unit,
    onOpenFilePicker: () -> Unit,
    onDeleteBookmarkSnapshot: (String) -> Unit,
    onBookmarkDisplayTypeToggle: () -> Unit,
    onAddFolder: (String, List<Int>?) -> Unit,
    onAddBookmark: (String, String, List<Int>?) -> Unit,
    onRenameBookmarkSnapshot: (String, String) -> Unit,
    onDeleteBookmarkItem: (List<Int>) -> Unit,
    onUpdateBookmarkItem: (List<Int>, String, String?) -> Unit,
    onAddEmptyBookmarkSnapshot: () -> Unit,
) {
    val uiState = rememberHomeScreenUiState()
    var showImportOptionDialog by uiState.showImportOptionDialog
    var isBrowserEditMode by uiState.isBrowserEditMode
    var showDefaultBrowserDialog by uiState.showDefaultBrowserDialog
    var showColorPickerDialog by uiState.showColorPickerDialog
    var showAddItemTypeDialog by uiState.showAddItemTypeDialog
    var showShareBookmarkMethodDialog by uiState.showShareBookmarkMethodDialog
    var showAddFolderDialog by uiState.showAddFolderDialog
    var showAddBookmarkDialog by uiState.showAddBookmarkDialog
    var pendingFolderTitle by uiState.pendingFolderTitle
    var pendingBookmarkTitle by uiState.pendingBookmarkTitle
    var pendingBookmarkUrl by uiState.pendingBookmarkUrl
    var pendingDeleteSnapshotId by uiState.pendingDeleteSnapshotId
    var pendingRenameSnapshotId by uiState.pendingRenameSnapshotId
    var pendingSnapshotTitle by uiState.pendingSnapshotTitle
    var pendingEditBookmarkItemPath by uiState.pendingEditBookmarkItemPath
    var pendingDeleteBookmarkItemPath by uiState.pendingDeleteBookmarkItemPath
    var pendingEditBookmarkItem by uiState.pendingEditBookmarkItem
    var pendingEditBookmarkTitle by uiState.pendingEditBookmarkTitle
    var pendingEditBookmarkUrl by uiState.pendingEditBookmarkUrl

    val orderedSnapshotIds = state.orderedSnapshotIds
    val context = LocalContext.current
    val resources = LocalResources.current

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { orderedSnapshotIds.size },
    )
    val selectedBookmarkId = state.selectedBookmarkId
        ?.takeIf { id -> orderedSnapshotIds.contains(id) }
        ?: orderedSnapshotIds.getOrNull(pagerState.currentPage)
        ?: orderedSnapshotIds.firstOrNull()
    val selectedFolderPath = state.selectedFolderPaths.pathOf(selectedBookmarkId)
    val snapshotTitles = remember(orderedSnapshotIds, state.bookmarkDocuments) {
        orderedSnapshotIds.mapIndexed { index, snapshotId ->
            val defaultTitle = "북마크${index + 1}"
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

    LaunchedEffect(pagerState, orderedSnapshotIds) {
        if (orderedSnapshotIds.isEmpty()) return@LaunchedEffect
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                orderedSnapshotIds.getOrNull(page)?.let(onSnapshotSelected)
            }
    }

    HomeScreenBackHandler(uiState = uiState)
    HomeDialogHost(
        state = state,
        uiState = uiState,
        selectedBookmarkId = selectedBookmarkId,
        selectedBookmarkDocument = selectedBookmarkDocument,
        selectedFolderPath = selectedFolderPath,
        onOpenBookmarkImportGuide = onOpenBookmarkImportGuide,
        onOpenFilePicker = onOpenFilePicker,
        onAddEmptyBookmarkSnapshot = onAddEmptyBookmarkSnapshot,
        onDeleteBookmarkSnapshot = onDeleteBookmarkSnapshot,
        onRenameBookmarkSnapshot = onRenameBookmarkSnapshot,
        onUpdateBookmarkItem = onUpdateBookmarkItem,
        onDeleteBookmarkItem = onDeleteBookmarkItem,
        onAddFolder = onAddFolder,
        onAddBookmark = onAddBookmark,
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
                    if (!onOpenBookmark(url, state.defaultBrowserPackage)) {
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
                },
                onSelectedFolderPathChange = onSelectedFolderPathChange,
                currentSnapshotTitle = selectedBookmarkId?.let(snapshotTitles::get),
                onSnapshotTitleClick = {
                    if (selectedBookmarkId != null) {
                        pendingRenameSnapshotId = selectedBookmarkId
                        pendingSnapshotTitle = snapshotTitles[selectedBookmarkId].orEmpty()
                    }
                },
                onSnapshotShareClick = { showShareBookmarkMethodDialog = true },
            )
        }
    }
}

internal class HomeScreenUiState(
    val showImportOptionDialog: MutableState<Boolean>,
    val isBrowserEditMode: MutableState<Boolean>,
    val showDefaultBrowserDialog: MutableState<Boolean>,
    val showColorPickerDialog: MutableState<Boolean>,
    val showAddItemTypeDialog: MutableState<Boolean>,
    val showShareBookmarkMethodDialog: MutableState<Boolean>,
    val showAddFolderDialog: MutableState<Boolean>,
    val showAddBookmarkDialog: MutableState<Boolean>,
    val pendingFolderTitle: MutableState<String>,
    val pendingBookmarkTitle: MutableState<String>,
    val pendingBookmarkUrl: MutableState<String>,
    val pendingDeleteSnapshotId: MutableState<String?>,
    val pendingRenameSnapshotId: MutableState<String?>,
    val pendingSnapshotTitle: MutableState<String>,
    val pendingEditBookmarkItemPath: MutableState<List<Int>?>,
    val pendingDeleteBookmarkItemPath: MutableState<List<Int>?>,
    val pendingEditBookmarkItem: MutableState<BookmarkItem?>,
    val pendingEditBookmarkTitle: MutableState<String>,
    val pendingEditBookmarkUrl: MutableState<String>,
)

@Composable
private fun rememberHomeScreenUiState(): HomeScreenUiState = HomeScreenUiState(
    showImportOptionDialog = rememberSaveable { mutableStateOf(false) },
    isBrowserEditMode = rememberSaveable { mutableStateOf(false) },
    showDefaultBrowserDialog = rememberSaveable { mutableStateOf(false) },
    showColorPickerDialog = rememberSaveable { mutableStateOf(false) },
    showAddItemTypeDialog = rememberSaveable { mutableStateOf(false) },
    showShareBookmarkMethodDialog = rememberSaveable { mutableStateOf(false) },
    showAddFolderDialog = rememberSaveable { mutableStateOf(false) },
    showAddBookmarkDialog = rememberSaveable { mutableStateOf(false) },
    pendingFolderTitle = rememberSaveable { mutableStateOf("") },
    pendingBookmarkTitle = rememberSaveable { mutableStateOf("") },
    pendingBookmarkUrl = rememberSaveable { mutableStateOf("") },
    pendingDeleteSnapshotId = rememberSaveable { mutableStateOf<String?>(null) },
    pendingRenameSnapshotId = rememberSaveable { mutableStateOf<String?>(null) },
    pendingSnapshotTitle = rememberSaveable { mutableStateOf("") },
    pendingEditBookmarkItemPath = remember { mutableStateOf<List<Int>?>(null) },
    pendingDeleteBookmarkItemPath = remember { mutableStateOf<List<Int>?>(null) },
    pendingEditBookmarkItem = remember { mutableStateOf<BookmarkItem?>(null) },
    pendingEditBookmarkTitle = rememberSaveable { mutableStateOf("") },
    pendingEditBookmarkUrl = rememberSaveable { mutableStateOf("") },
)

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        state = HomeState(
            orderedSnapshotIds = emptyList(),
            bookmarkDisplayType = BookmarkDisplayType.LIST,
        ),
        onSettingsClick = {},
        onOpenBookmark = { _, _ -> true },
        onOpenBookmarkImportGuide = {},
        onSnapshotSelected = {},
        onSelectedFolderPathChange = { _, _ -> },
        onBookmarkColorSelected = { _, _ -> },
        onDefaultBrowserSelected = {},
        onOpenFilePicker = {},
        onDeleteBookmarkSnapshot = {},
        onBookmarkDisplayTypeToggle = {},
        onAddFolder = { _, _ -> },
        onAddBookmark = { _, _, _ -> },
        onRenameBookmarkSnapshot = { _, _ -> },
        onDeleteBookmarkItem = {},
        onUpdateBookmarkItem = { _, _, _ -> },
        onAddEmptyBookmarkSnapshot = {},
    )
}
