package com.hdw.bookmarker.feature.home.presentation

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
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.home.domain.model.AddBookmarkItemRequest
import com.hdw.bookmarker.feature.home.domain.model.QuickSaveBookmarkSeed
import com.hdw.bookmarker.feature.home.domain.model.UpdateBookmarkItemRequest
import com.hdw.bookmarker.feature.home.domain.search.model.BookmarkSearchItemType
import com.hdw.bookmarker.feature.home.domain.search.model.BookmarkSearchResult
import com.hdw.bookmarker.feature.home.presentation.component.bookmark.HomeContent
import com.hdw.bookmarker.feature.home.presentation.component.bookmark.HomeScreenBackHandler
import com.hdw.bookmarker.feature.home.presentation.component.bookmark.export.BookmarkExportAction
import com.hdw.bookmarker.feature.home.presentation.component.search.BookmarkSearchDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.HomeDialogHost
import com.hdw.bookmarker.feature.home.presentation.model.BookmarkDisplayType
import com.hdw.bookmarker.feature.home.presentation.model.HomeState
import com.hdw.bookmarker.feature.home.presentation.model.rememberHomeScreenUiState
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
    onMoveInboxBookmark: (SnapshotId, List<Int>, SnapshotId, List<Int>?) -> Unit,
    onAddEmptyBookmarkSnapshot: () -> Unit,
    onBookmarkExportRequest: (BookmarkExportAction, com.hdw.bookmarker.core.model.bookmark.BookmarkDocument) -> Unit,
) {
    val uiState = rememberHomeScreenUiState()
    var showAddItemTypeDialog by uiState.showAddItemTypeDialog
    var showImportOptionDialog by uiState.showImportOptionDialog
    var showDefaultBrowserDialog by uiState.showDefaultBrowserDialog
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
    var pendingActionSnapshotId by uiState.pendingActionSnapshotId
    var pendingActionSnapshotTitle by uiState.pendingActionSnapshotTitle
    var pendingRenameSnapshotId by uiState.pendingRenameSnapshotId
    var pendingSnapshotTitle by uiState.pendingSnapshotTitle
    var pendingBookmarkExportAction by uiState.pendingBookmarkExportAction
    var pendingEditBookmarkItem by uiState.pendingEditBookmarkItem
    var pendingEditBookmarkItemPath by uiState.pendingEditBookmarkItemPath
    var pendingEditBookmarkTitle by uiState.pendingEditBookmarkTitle
    var pendingEditBookmarkUrl by uiState.pendingEditBookmarkUrl
    var pendingEditBookmarkTags by uiState.pendingEditBookmarkTags
    var pendingEditBookmarkDescription by uiState.pendingEditBookmarkDescription
    var copiedBookmarkItem by uiState.copiedBookmarkItem
    var pendingPasteFolderPath by uiState.pendingPasteFolderPath
    val orderedSnapshotIds = remember(state.bookmarkSnapshots) {
        state.bookmarkSnapshots.orderedIds.filter { id -> !state.bookmarkSnapshots.isEmptyInbox(id) }
    }
    val context = LocalContext.current

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { orderedSnapshotIds.size },
    )
    val scope = rememberCoroutineScope()
    val selectedBookmarkId = orderedSnapshotIds.getOrNull(pagerState.currentPage)
        ?: state.selectedBookmarkId
            ?.takeIf { id -> orderedSnapshotIds.contains(id) }
        ?: orderedSnapshotIds.firstOrNull()
    val selectedFolderPath = state.selectedFolderPaths.pathOf(selectedBookmarkId)
    val defaultSnapshotTitlePrefix = stringResource(R.string.default_snapshot_title_prefix)
    val copyFolderToastFormat = stringResource(R.string.bookmark_item_copy_toast_folder)
    val copyBookmarkToastFormat = stringResource(R.string.bookmark_item_copy_toast_bookmark)
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
    val onSnapshotTabClick = fun(snapshotId: SnapshotId) {
        if (uiState.isSelectingMoveDestination.value) {
            val sourceSnapshotId = uiState.pendingMoveSourceSnapshotId.value
            val canSelectAsMoveTarget =
                snapshotId != sourceSnapshotId && !state.bookmarkSnapshots.isInbox(snapshotId)
            if (!canSelectAsMoveTarget) {
                return
            }
            uiState.pendingMoveTargetSnapshotId.value = snapshotId
            return
        }

        val targetPage = orderedSnapshotIds.indexOf(snapshotId)
        if (targetPage >= 0 && targetPage != pagerState.currentPage) {
            scope.launch {
                pagerState.animateScrollToPage(targetPage)
            }
        }
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

    LaunchedEffect(uiState.pendingMoveTargetSnapshotId.value) {
        val targetSnapshotId = uiState.pendingMoveTargetSnapshotId.value ?: return@LaunchedEffect
        val sourceSnapshotId = uiState.pendingMoveSourceSnapshotId.value ?: return@LaunchedEffect
        val sourcePath = uiState.pendingMoveBookmarkPath.value ?: return@LaunchedEffect
        val targetFolderPath = state.selectedFolderPaths.pathOf(targetSnapshotId)
        onMoveInboxBookmark(
            sourceSnapshotId,
            sourcePath,
            targetSnapshotId,
            targetFolderPath,
        )
        uiState.pendingMoveBookmarkItem.value = null
        uiState.pendingMoveBookmarkPath.value = null
        uiState.pendingMoveSourceSnapshotId.value = null
        uiState.pendingMoveTargetSnapshotId.value = null
        uiState.isSelectingMoveDestination.value = false
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
        onCopyBookmarkItem = { item ->
            copiedBookmarkItem = item.deepCopy()
            val shortenedTitle = item.displayTitle().truncateForToast()
            val toastMessage = when (item) {
                is BookmarkItem.Folder -> copyFolderToastFormat.format(shortenedTitle)
                is BookmarkItem.Bookmark -> copyBookmarkToastFormat.format(shortenedTitle)
            }
            context.showShortToast(toastMessage)
        },
        onPasteCopiedItem = { item, targetFolderPath ->
            val request = when (item) {
                is BookmarkItem.Bookmark -> AddBookmarkItemRequest.Bookmark(
                    parentFolderPath = targetFolderPath,
                    title = item.title,
                    url = item.url,
                    description = item.description.orEmpty(),
                    tags = item.tags,
                )

                is BookmarkItem.Folder -> AddBookmarkItemRequest.Folder(
                    parentFolderPath = targetFolderPath,
                    title = item.title,
                    description = item.description.orEmpty(),
                    children = item.children,
                )
            }
            onAddBookmarkItem(request)
        },
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
                defaultBrowserIcon = defaultBrowserIcon,
                onSettingsClick = onSettingsClick,
                onSearchClick = { showSearchDialog = true },
                onBookmarkDisplayTypeToggle = onBookmarkDisplayTypeToggle,
                onDefaultBrowserPickerOpen = {
                    if (state.installedBrowsers.isNotEmpty()) {
                        showDefaultBrowserDialog = true
                    }
                },
                onAddItemClick = { showAddItemTypeDialog = true },
                onImportClick = { showImportOptionDialog = true },
                onSnapshotClick = onSnapshotTabClick,
                onSnapshotLongClick = { snapshotId ->
                    pendingActionSnapshotId = snapshotId
                    pendingActionSnapshotTitle = snapshotTitles[snapshotId].orEmpty()
                    uiState.showSnapshotActionDialog.value = true
                },
                onBookmarkClick = { url ->
                    if (enableLargeScreenSidePreview) {
                        uiState.previewPaneState.open(url)
                    } else if (!onOpenBookmark(url, state.defaultBrowserPackage)) {
                        context.showShortToast(R.string.open_bookmark_failed)
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
                onBlankAreaLongClick = { targetFolderPath ->
                    if (copiedBookmarkItem != null) {
                        pendingPasteFolderPath = targetFolderPath
                        uiState.showPasteActionDialog.value = true
                    }
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
                        context.showShortToast(R.string.open_bookmark_failed)
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

private fun BookmarkItem.deepCopy(): BookmarkItem = when (this) {
    is BookmarkItem.Bookmark -> copy(tags = tags.toList())
    is BookmarkItem.Folder -> copy(children = children.map(BookmarkItem::deepCopy))
}

private fun BookmarkItem.displayTitle(): String = when (this) {
    is BookmarkItem.Bookmark -> title
    is BookmarkItem.Folder -> title
}

private fun String.truncateForToast(maxLength: Int = 16): String {
    if (length <= maxLength) return this
    if (maxLength <= 3) return "..."
    return take(maxLength - 3) + "..."
}
