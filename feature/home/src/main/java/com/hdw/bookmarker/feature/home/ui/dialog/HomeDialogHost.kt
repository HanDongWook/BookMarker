package com.hdw.bookmarker.feature.home.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.hdw.bookmarker.core.domain.util.BookmarkColorGenerator
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.feature.home.contract.AddBookmarkItemRequest
import com.hdw.bookmarker.feature.home.contract.HomeState
import com.hdw.bookmarker.feature.home.contract.UpdateBookmarkItemRequest
import com.hdw.bookmarker.feature.home.ui.HomeScreenUiState
import com.hdw.bookmarker.feature.home.ui.share.BookmarkExportAction
import com.hdw.bookmarker.feature.home.ui.share.ExportBookmarkMethodDialog

@Composable
internal fun HomeDialogHost(
    state: HomeState,
    uiState: HomeScreenUiState,
    selectedBookmarkId: String?,
    selectedFolderPath: List<Int>?,
    onOpenBookmarkImportGuide: () -> Unit,
    onAddEmptyBookmarkSnapshot: () -> Unit,
    onDeleteBookmarkSnapshot: (String) -> Unit,
    onRenameBookmarkSnapshot: (String, String) -> Unit,
    onUpdateBookmarkItem: (UpdateBookmarkItemRequest) -> Unit,
    onDeleteBookmarkItem: (List<Int>) -> Unit,
    onAddBookmarkItem: (AddBookmarkItemRequest) -> Unit,
    onDefaultBrowserSelected: (String) -> Unit,
    onBookmarkColorSelected: (String, Long) -> Unit,
) {
    var showImportOptionDialog by uiState.showImportOptionDialog
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
    var pendingEditBookmarkItemPath by uiState.pendingEditBookmarkItemPath
    var pendingDeleteBookmarkItemPath by uiState.pendingDeleteBookmarkItemPath
    var pendingEditBookmarkItem by uiState.pendingEditBookmarkItem
    var pendingEditBookmarkTitle by uiState.pendingEditBookmarkTitle
    var pendingEditBookmarkUrl by uiState.pendingEditBookmarkUrl
    var pendingEditBookmarkDescription by uiState.pendingEditBookmarkDescription

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
            onOpenImportBookmarks = {
                showImportOptionDialog = false
                onOpenBookmarkImportGuide()
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
            description = pendingEditBookmarkDescription,
            onTitleChange = { pendingEditBookmarkTitle = it },
            onUrlChange = { pendingEditBookmarkUrl = it },
            onDescriptionChange = { pendingEditBookmarkDescription = it },
            onDismiss = {
                pendingEditBookmarkItemPath = null
                pendingEditBookmarkItem = null
            },
            onApply = {
                val path = pendingEditBookmarkItemPath ?: return@ManageBookmarkItemDialog
                val request = when (editingItem) {
                    is BookmarkItem.Bookmark -> UpdateBookmarkItemRequest.Bookmark(
                        path = path,
                        title = pendingEditBookmarkTitle,
                        url = pendingEditBookmarkUrl,
                    )

                    is BookmarkItem.Folder -> UpdateBookmarkItemRequest.Folder(
                        path = path,
                        title = pendingEditBookmarkTitle,
                        description = pendingEditBookmarkDescription,
                    )
                }
                onUpdateBookmarkItem(request)
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
                pendingFolderDescription = ""
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

    if (showExportBookmarkMethodDialog) {
        ExportBookmarkMethodDialog(
            onDismiss = { showExportBookmarkMethodDialog = false },
            onShareTextClick = {
                showExportBookmarkMethodDialog = false
                pendingBookmarkExportAction = BookmarkExportAction.ShareText
            },
            onShareHtmlClick = {
                showExportBookmarkMethodDialog = false
                pendingBookmarkExportAction = BookmarkExportAction.ShareHtml
            },
            onSaveTextClick = {
                showExportBookmarkMethodDialog = false
                pendingBookmarkExportAction = BookmarkExportAction.SaveText
            },
            onSaveHtmlClick = {
                showExportBookmarkMethodDialog = false
                pendingBookmarkExportAction = BookmarkExportAction.SaveHtml
            },
        )
    }

    if (showAddFolderDialog) {
        AddFolderDialog(
            folderTitle = pendingFolderTitle,
            folderDescription = pendingFolderDescription,
            onFolderTitleChange = { pendingFolderTitle = it },
            onFolderDescriptionChange = { pendingFolderDescription = it },
            onDismiss = { showAddFolderDialog = false },
            onConfirm = {
                onAddBookmarkItem(
                    AddBookmarkItemRequest.Folder(
                        parentFolderPath = selectedFolderPath,
                        title = pendingFolderTitle,
                        description = pendingFolderDescription,
                    ),
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
                onAddBookmarkItem(
                    AddBookmarkItemRequest.Bookmark(
                        parentFolderPath = selectedFolderPath,
                        title = pendingBookmarkTitle,
                        url = pendingBookmarkUrl,
                    ),
                )
                showAddBookmarkDialog = false
            },
        )
    }
}
