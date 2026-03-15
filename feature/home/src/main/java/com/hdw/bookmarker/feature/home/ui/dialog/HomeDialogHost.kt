package com.hdw.bookmarker.feature.home.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.hdw.bookmarker.core.domain.util.BookmarkColorGenerator
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.feature.home.contract.AddBookmarkItemRequest
import com.hdw.bookmarker.feature.home.contract.HomeState
import com.hdw.bookmarker.feature.home.contract.UpdateBookmarkItemRequest
import com.hdw.bookmarker.feature.home.ui.HomeScreenUiState
import com.hdw.bookmarker.feature.home.ui.export.BookmarkExportAction
import com.hdw.bookmarker.feature.home.ui.export.BookmarkExportFormat
import com.hdw.bookmarker.feature.home.ui.export.BookmarkExportMethod
import com.hdw.bookmarker.feature.home.ui.export.ExportBookmarkMethodDialog

private fun parseBookmarkTags(rawValue: String): List<String> = rawValue
    .split(",")
    .map(String::trim)
    .filter(String::isNotBlank)
    .distinct()

@Composable
internal fun HomeDialogHost(
    state: HomeState,
    uiState: HomeScreenUiState,
    selectedBookmarkId: SnapshotId?,
    selectedFolderPath: List<Int>?,
    onOpenBookmarkImportGuide: () -> Unit,
    onAddEmptyBookmarkSnapshot: () -> Unit,
    onDeleteBookmarkSnapshot: (SnapshotId) -> Unit,
    onRenameBookmarkSnapshot: (SnapshotId, String) -> Unit,
    onUpdateBookmarkItem: (UpdateBookmarkItemRequest) -> Unit,
    onDeleteBookmarkItem: (List<Int>) -> Unit,
    onAddBookmarkItem: (AddBookmarkItemRequest) -> Unit,
    onDefaultBrowserSelected: (String) -> Unit,
    onBookmarkColorSelected: (SnapshotId, Long) -> Unit,
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
    var pendingBookmarkDescription by uiState.pendingBookmarkDescription
    var pendingBookmarkTags by uiState.pendingBookmarkTags
    var addBookmarkToInbox by uiState.addBookmarkToInbox
    var pendingDeleteSnapshotId by uiState.pendingDeleteSnapshotId
    var pendingRenameSnapshotId by uiState.pendingRenameSnapshotId
    var pendingSnapshotTitle by uiState.pendingSnapshotTitle
    var pendingEditBookmarkItemPath by uiState.pendingEditBookmarkItemPath
    var pendingDeleteBookmarkItemPath by uiState.pendingDeleteBookmarkItemPath
    var pendingEditBookmarkItem by uiState.pendingEditBookmarkItem
    var pendingEditBookmarkTitle by uiState.pendingEditBookmarkTitle
    var pendingEditBookmarkUrl by uiState.pendingEditBookmarkUrl
    var pendingEditBookmarkTags by uiState.pendingEditBookmarkTags
    var pendingEditBookmarkDescription by uiState.pendingEditBookmarkDescription
    var showBookmarkItemActionDialog by uiState.showBookmarkItemActionDialog
    var showEditBookmarkItemDialog by uiState.showEditBookmarkItemDialog

    if (showColorPickerDialog && selectedBookmarkId != null) {
        BookmarkColorPickerDialog(
            colors = BookmarkColorGenerator.getAllColors(),
            currentColor = state.bookmarkColors[selectedBookmarkId]
                ?: BookmarkColorGenerator.generateColorForId(selectedBookmarkId.value),
            onColorSelect = { color ->
                onBookmarkColorSelected(selectedBookmarkId, color)
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

    if (showEditBookmarkItemDialog && pendingEditBookmarkItemPath != null && pendingEditBookmarkItem != null) {
        val editingItem = pendingEditBookmarkItem!!
        ManageBookmarkItemDialog(
            item = editingItem,
            title = pendingEditBookmarkTitle,
            url = pendingEditBookmarkUrl,
            tags = pendingEditBookmarkTags,
            description = pendingEditBookmarkDescription,
            onTitleChange = { pendingEditBookmarkTitle = it },
            onUrlChange = { pendingEditBookmarkUrl = it },
            onTagsChange = { pendingEditBookmarkTags = it },
            onDescriptionChange = { pendingEditBookmarkDescription = it },
            onDismiss = {
                showEditBookmarkItemDialog = false
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
                        description = pendingEditBookmarkDescription,
                        tags = parseBookmarkTags(pendingEditBookmarkTags),
                    )

                    is BookmarkItem.Folder -> UpdateBookmarkItemRequest.Folder(
                        path = path,
                        title = pendingEditBookmarkTitle,
                        description = pendingEditBookmarkDescription,
                    )
                }
                onUpdateBookmarkItem(request)
                showEditBookmarkItemDialog = false
                pendingEditBookmarkItemPath = null
                pendingEditBookmarkItem = null
            },
        )
    }

    if (showBookmarkItemActionDialog && pendingEditBookmarkItemPath != null && pendingEditBookmarkItem != null) {
        BookmarkItemActionDialog(
            onDismiss = {
                showBookmarkItemActionDialog = false
                pendingEditBookmarkItemPath = null
                pendingEditBookmarkItem = null
            },
            onEditClick = {
                showBookmarkItemActionDialog = false
                showEditBookmarkItemDialog = true
            },
            onDeleteClick = {
                showBookmarkItemActionDialog = false
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
                pendingBookmarkDescription = ""
                pendingBookmarkTags = ""
                addBookmarkToInbox = false
                showAddBookmarkDialog = true
            },
        )
    }

    if (showExportBookmarkMethodDialog) {
        ExportBookmarkMethodDialog(
            onDismiss = { showExportBookmarkMethodDialog = false },
            onShareTextClick = {
                showExportBookmarkMethodDialog = false
                pendingBookmarkExportAction = BookmarkExportAction(
                    method = BookmarkExportMethod.SHARE,
                    format = BookmarkExportFormat.TEXT,
                )
            },
            onShareHtmlClick = {
                showExportBookmarkMethodDialog = false
                pendingBookmarkExportAction = BookmarkExportAction(
                    method = BookmarkExportMethod.SHARE,
                    format = BookmarkExportFormat.HTML,
                )
            },
            onSaveTextClick = {
                showExportBookmarkMethodDialog = false
                pendingBookmarkExportAction = BookmarkExportAction(
                    method = BookmarkExportMethod.SAVE,
                    format = BookmarkExportFormat.TEXT,
                )
            },
            onSaveHtmlClick = {
                showExportBookmarkMethodDialog = false
                pendingBookmarkExportAction = BookmarkExportAction(
                    method = BookmarkExportMethod.SAVE,
                    format = BookmarkExportFormat.HTML,
                )
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
            bookmarkDescription = pendingBookmarkDescription,
            bookmarkTags = pendingBookmarkTags,
            onBookmarkTitleChange = { pendingBookmarkTitle = it },
            onBookmarkUrlChange = { pendingBookmarkUrl = it },
            onBookmarkDescriptionChange = { pendingBookmarkDescription = it },
            onBookmarkTagsChange = { pendingBookmarkTags = it },
            onDismiss = {
                showAddBookmarkDialog = false
                addBookmarkToInbox = false
            },
            onConfirm = {
                onAddBookmarkItem(
                    AddBookmarkItemRequest.Bookmark(
                        parentFolderPath = selectedFolderPath.takeUnless { addBookmarkToInbox },
                        title = pendingBookmarkTitle,
                        url = pendingBookmarkUrl,
                        description = pendingBookmarkDescription,
                        tags = parseBookmarkTags(pendingBookmarkTags),
                        saveToInbox = addBookmarkToInbox,
                    ),
                )
                showAddBookmarkDialog = false
                addBookmarkToInbox = false
            },
        )
    }
}
