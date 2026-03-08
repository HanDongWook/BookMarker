package com.hdw.bookmarker.feature.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hdw.bookmarker.core.domain.util.BookmarkColorGenerator
import com.hdw.bookmarker.core.model.MimeTypes
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.home.bookmarkdisplay.BookmarkDisplayType
import com.hdw.bookmarker.feature.home.dialog.AddBookmarkDialog
import com.hdw.bookmarker.feature.home.dialog.AddFolderDialog
import com.hdw.bookmarker.feature.home.dialog.AddItemTypeDialog
import com.hdw.bookmarker.feature.home.dialog.BookmarkColorPickerDialog
import com.hdw.bookmarker.feature.home.dialog.DefaultBrowserPickerDialog
import com.hdw.bookmarker.feature.home.dialog.DeleteBookmarkItemDialog
import com.hdw.bookmarker.feature.home.dialog.DeleteBookmarkSnapshotDialog
import com.hdw.bookmarker.feature.home.dialog.ImportOptionDialog
import com.hdw.bookmarker.feature.home.dialog.ManageBookmarkItemDialog
import com.hdw.bookmarker.feature.home.dialog.RenameBookmarkSnapshotDialog
import com.hdw.bookmarker.feature.home.sharebookmark.ShareBookmarkMethodDialog
import com.hdw.bookmarker.feature.home.sharebookmark.requestCurrentBookmarkHtmlShare
import com.hdw.bookmarker.feature.home.sharebookmark.requestCurrentBookmarkTextShare
import kotlinx.coroutines.flow.distinctUntilChanged
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeRoute(
    onSettingsClick: () -> Unit,
    onOpenBookmark: (String, String?) -> Boolean,
    onOpenBookmarkImportGuide: () -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val resources = LocalResources.current
    val htmlPickerLauncher = rememberLauncherForActivityResult(OpenDocument()) { uri ->
        if (uri != null) {
            viewModel.onHtmlFileSelected(uri)
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is HomeSideEffect.ShowError -> {
                val message = resources.getString(sideEffect.messageResId)
                val toastText = if (sideEffect.detail.isNullOrBlank()) {
                    message
                } else {
                    resources.getString(R.string.error_with_detail, message, sideEffect.detail)
                }
                context.showShortToast(toastText)
            }

            is HomeSideEffect.ShowMessage -> {
                context.showShortToast(resources.getString(sideEffect.messageResId))
            }

            is HomeSideEffect.OpenFilePicker -> {
                htmlPickerLauncher.launch(arrayOf(MimeTypes.HTML))
            }
        }
    }

    HomeScreen(
        state = state,
        onSettingsClick = onSettingsClick,
        onOpenBookmark = onOpenBookmark,
        onOpenBookmarkImportGuide = onOpenBookmarkImportGuide,
        onSnapshotSelected = viewModel::onSnapshotSelected,
        onBookmarkColorSelected = viewModel::onBookmarkColorSelected,
        onDefaultBrowserSelected = viewModel::onDefaultBrowserSelected,
        onOpenFilePicker = viewModel::openFilePicker,
        onDeleteBookmarkSnapshot = viewModel::deleteBookmarkSnapshot,
        onBookmarkDisplayTypeToggle = viewModel::onBookmarkDisplayTypeToggle,
        onAddFolder = viewModel::addFolder,
        onAddBookmark = viewModel::addBookmark,
        onRenameBookmarkSnapshot = viewModel::renameBookmarkSnapshot,
        onDeleteBookmarkItem = viewModel::deleteBookmarkItem,
        onUpdateBookmarkItem = viewModel::updateBookmarkItem,
        onAddEmptyBookmarkSnapshot = viewModel::addEmptyBookmarkSnapshot,
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    onSettingsClick: () -> Unit,
    onOpenBookmark: (String, String?) -> Boolean,
    onOpenBookmarkImportGuide: () -> Unit,
    onSnapshotSelected: (String) -> Unit,
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
    val orderedSnapshotIds = state.orderedSnapshotIds
    val context = LocalContext.current
    val resources = LocalResources.current
    var showImportOptionDialog by rememberSaveable { mutableStateOf(false) }
    var isBrowserEditMode by rememberSaveable { mutableStateOf(false) }
    var showDefaultBrowserDialog by rememberSaveable { mutableStateOf(false) }
    var showColorPickerDialog by rememberSaveable { mutableStateOf(false) }
    var showAddItemTypeDialog by rememberSaveable { mutableStateOf(false) }
    var showShareBookmarkMethodDialog by rememberSaveable { mutableStateOf(false) }
    var showAddFolderDialog by rememberSaveable { mutableStateOf(false) }
    var showAddBookmarkDialog by rememberSaveable { mutableStateOf(false) }
    var pendingFolderTitle by rememberSaveable { mutableStateOf("") }
    var pendingBookmarkTitle by rememberSaveable { mutableStateOf("") }
    var pendingBookmarkUrl by rememberSaveable { mutableStateOf("") }
    var pendingDeleteSnapshotId by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingRenameSnapshotId by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingSnapshotTitle by rememberSaveable { mutableStateOf("") }
    var pendingEditBookmarkItemPath by remember { mutableStateOf<List<Int>?>(null) }
    var pendingDeleteBookmarkItemPath by remember { mutableStateOf<List<Int>?>(null) }
    var pendingEditBookmarkItem by remember { mutableStateOf<BookmarkItem?>(null) }
    var pendingEditBookmarkTitle by rememberSaveable { mutableStateOf("") }
    var pendingEditBookmarkUrl by rememberSaveable { mutableStateOf("") }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { orderedSnapshotIds.size },
    )
    val selectedBookmarkId = state.selectedBookmarkId
        ?.takeIf { id -> orderedSnapshotIds.contains(id) }
        ?: orderedSnapshotIds.getOrNull(pagerState.currentPage)
        ?: orderedSnapshotIds.firstOrNull()
    var selectedFolderPath by rememberSaveable(selectedBookmarkId) { mutableStateOf<IntArray?>(null) }
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

    BackHandler(enabled = showImportOptionDialog) {
        showImportOptionDialog = false
    }

    BackHandler(enabled = isBrowserEditMode) {
        isBrowserEditMode = false
    }

    BackHandler(enabled = showColorPickerDialog) {
        showColorPickerDialog = false
    }

    BackHandler(enabled = showAddItemTypeDialog) {
        showAddItemTypeDialog = false
    }
    BackHandler(enabled = showShareBookmarkMethodDialog) {
        showShareBookmarkMethodDialog = false
    }

    BackHandler(enabled = showAddFolderDialog) {
        showAddFolderDialog = false
    }

    BackHandler(enabled = showAddBookmarkDialog) {
        showAddBookmarkDialog = false
    }

    BackHandler(enabled = pendingEditBookmarkItemPath != null) {
        pendingEditBookmarkItemPath = null
        pendingEditBookmarkItem = null
    }

    BackHandler(enabled = pendingDeleteBookmarkItemPath != null) {
        pendingDeleteBookmarkItemPath = null
    }

    if (showColorPickerDialog && selectedBookmarkId != null) {
        BookmarkColorPickerDialog(
            colors = BookmarkColorGenerator.getAllColors(),
            currentColor = state.bookmarkColors[selectedBookmarkId]
                ?: BookmarkColorGenerator.generateColorForId(selectedBookmarkId),
            onColorSelect = { color ->
                onBookmarkColorSelected(selectedBookmarkId, color)
                showColorPickerDialog = false
            },
            onDismiss = { showColorPickerDialog = false },
        )
    }

    if (showDefaultBrowserDialog) {
        DefaultBrowserPickerDialog(
            installedBrowsers = state.installedBrowsers,
            selectedPackage = state.defaultBrowserPackage,
            onSelect = { packageName ->
                onDefaultBrowserSelected(packageName)
                showDefaultBrowserDialog = false
            },
            onDismiss = {
                showDefaultBrowserDialog = false
            },
        )
    }

    if (showImportOptionDialog) {
        ImportOptionDialog(
            onDismiss = { showImportOptionDialog = false },
            onOpenGuide = {
                showImportOptionDialog = false
                onOpenBookmarkImportGuide()
            },
            onPickFile = {
                showImportOptionDialog = false
                onOpenFilePicker()
            },
            onAddEmptyBookmarkItem = {
                showImportOptionDialog = false
                onAddEmptyBookmarkSnapshot()
            },
        )
    }

    if (pendingDeleteSnapshotId != null) {
        DeleteBookmarkSnapshotDialog(
            onDismiss = { pendingDeleteSnapshotId = null },
            onConfirmDelete = {
                onDeleteBookmarkSnapshot(pendingDeleteSnapshotId ?: return@DeleteBookmarkSnapshotDialog)
                pendingDeleteSnapshotId = null
            },
        )
    }

    if (pendingRenameSnapshotId != null) {
        RenameBookmarkSnapshotDialog(
            snapshotTitle = pendingSnapshotTitle,
            onSnapshotTitleChange = { pendingSnapshotTitle = it },
            onDismiss = { pendingRenameSnapshotId = null },
            onConfirm = {
                val snapshotId = pendingRenameSnapshotId ?: return@RenameBookmarkSnapshotDialog
                onRenameBookmarkSnapshot(snapshotId, pendingSnapshotTitle)
                pendingRenameSnapshotId = null
            },
        )
    }

    if (pendingEditBookmarkItemPath != null && pendingEditBookmarkItem != null) {
        val editingItem = pendingEditBookmarkItem!!
        ManageBookmarkItemDialog(
            item = editingItem,
            title = pendingEditBookmarkTitle,
            url = pendingEditBookmarkUrl,
            onTitleChange = { pendingEditBookmarkTitle = it },
            onUrlChange = { pendingEditBookmarkUrl = it },
            onDismiss = {
                pendingEditBookmarkItemPath = null
                pendingEditBookmarkItem = null
            },
            onApply = {
                val path = pendingEditBookmarkItemPath ?: return@ManageBookmarkItemDialog
                onUpdateBookmarkItem(
                    path,
                    pendingEditBookmarkTitle,
                    pendingEditBookmarkUrl.takeIf { editingItem is BookmarkItem.Bookmark },
                )
                pendingEditBookmarkItemPath = null
                pendingEditBookmarkItem = null
            },
            onDelete = {
                pendingDeleteBookmarkItemPath = pendingEditBookmarkItemPath
                pendingEditBookmarkItemPath = null
                pendingEditBookmarkItem = null
            },
        )
    }

    if (pendingDeleteBookmarkItemPath != null) {
        DeleteBookmarkItemDialog(
            onDismiss = { pendingDeleteBookmarkItemPath = null },
            onConfirmDelete = {
                onDeleteBookmarkItem(pendingDeleteBookmarkItemPath ?: return@DeleteBookmarkItemDialog)
                pendingDeleteBookmarkItemPath = null
            },
        )
    }

    if (showAddItemTypeDialog) {
        AddItemTypeDialog(
            onDismiss = { showAddItemTypeDialog = false },
            onAddFolderClick = {
                showAddItemTypeDialog = false
                pendingFolderTitle = ""
                showAddFolderDialog = true
            },
            onAddBookmarkClick = {
                showAddItemTypeDialog = false
                pendingBookmarkTitle = ""
                pendingBookmarkUrl = ""
                showAddBookmarkDialog = true
            },
            onShareClick = {
                showAddItemTypeDialog = false
                showShareBookmarkMethodDialog = true
            },
        )
    }

    if (showShareBookmarkMethodDialog) {
        ShareBookmarkMethodDialog(
            onDismiss = { showShareBookmarkMethodDialog = false },
            onShareTextClick = {
                showShareBookmarkMethodDialog = false
                val currentDocument = selectedBookmarkDocument
                if (currentDocument == null || !requestCurrentBookmarkTextShare(context, currentDocument)) {
                    context.showShortToast(resources.getString(R.string.share_current_bookmarks_empty))
                }
            },
            onShareHtmlClick = {
                showShareBookmarkMethodDialog = false
                val currentDocument = selectedBookmarkDocument
                when {
                    currentDocument == null -> {
                        context.showShortToast(resources.getString(R.string.share_current_bookmarks_empty))
                    }

                    !requestCurrentBookmarkHtmlShare(context, currentDocument) -> {
                        context.showShortToast(resources.getString(R.string.share_current_bookmarks_html_failed))
                    }
                }
            },
        )
    }

    if (showAddFolderDialog) {
        AddFolderDialog(
            folderTitle = pendingFolderTitle,
            onFolderTitleChange = { pendingFolderTitle = it },
            onDismiss = { showAddFolderDialog = false },
            onConfirm = {
                onAddFolder(
                    pendingFolderTitle,
                    selectedFolderPath?.toList(),
                )
                showAddFolderDialog = false
            },
        )
    }

    if (showAddBookmarkDialog) {
        AddBookmarkDialog(
            bookmarkTitle = pendingBookmarkTitle,
            bookmarkUrl = pendingBookmarkUrl,
            onBookmarkTitleChange = { pendingBookmarkTitle = it },
            onBookmarkUrlChange = { pendingBookmarkUrl = it },
            onDismiss = { showAddBookmarkDialog = false },
            onConfirm = {
                onAddBookmark(
                    pendingBookmarkTitle,
                    pendingBookmarkUrl,
                    selectedFolderPath?.toList(),
                )
                showAddBookmarkDialog = false
            },
        )
    }

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
                onSelectedFolderPathChange = { path -> selectedFolderPath = path?.toIntArray() },
                currentSnapshotTitle = selectedBookmarkId?.let(snapshotTitles::get),
                onSnapshotTitleClick = {
                    if (selectedBookmarkId != null) {
                        pendingRenameSnapshotId = selectedBookmarkId
                        pendingSnapshotTitle = snapshotTitles[selectedBookmarkId].orEmpty()
                    }
                },
            )
        }
    }
}

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
