package com.hdw.bookmarker.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import com.hdw.bookmarker.core.domain.util.BookmarkColorGenerator
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.home.contract.HomeState
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

@Composable
internal fun HomeDialogHost(
    state: HomeState,
    uiState: HomeScreenUiState,
    selectedBookmarkId: String?,
    selectedBookmarkDocument: BookmarkDocument?,
    selectedFolderPath: List<Int>?,
    onOpenBookmarkImportGuide: () -> Unit,
    onOpenFilePicker: () -> Unit,
    onAddEmptyBookmarkSnapshot: () -> Unit,
    onDeleteBookmarkSnapshot: (String) -> Unit,
    onRenameBookmarkSnapshot: (String, String) -> Unit,
    onUpdateBookmarkItem: (List<Int>, String, String?) -> Unit,
    onDeleteBookmarkItem: (List<Int>) -> Unit,
    onAddFolder: (String, List<Int>?) -> Unit,
    onAddBookmark: (String, String, List<Int>?) -> Unit,
    onDefaultBrowserSelected: (String) -> Unit,
    onBookmarkColorSelected: (String, Long) -> Unit,
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    var showImportOptionDialog by uiState.showImportOptionDialog
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
            onDismiss = { showDefaultBrowserDialog = false },
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
                    selectedFolderPath,
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
                    selectedFolderPath,
                )
                showAddBookmarkDialog = false
            },
        )
    }
}
