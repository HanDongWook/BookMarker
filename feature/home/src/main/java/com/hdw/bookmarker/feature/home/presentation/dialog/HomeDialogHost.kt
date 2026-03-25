package com.hdw.bookmarker.feature.home.presentation.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.domain.util.BookmarkColorGenerator
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.home.domain.model.AddBookmarkItemRequest
import com.hdw.bookmarker.feature.home.domain.model.UpdateBookmarkItemRequest
import com.hdw.bookmarker.feature.home.presentation.component.bookmark.export.BookmarkExportAction
import com.hdw.bookmarker.feature.home.presentation.component.bookmark.export.BookmarkExportFormat
import com.hdw.bookmarker.feature.home.presentation.component.bookmark.export.BookmarkExportMethod
import com.hdw.bookmarker.feature.home.presentation.dialog.bookmark.AddBookmarkDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.bookmark.AddFolderDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.bookmark.AddItemTypeDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.bookmark.BookmarkItemActionDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.bookmark.DefaultBrowserPickerDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.bookmark.DeleteBookmarkItemDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.bookmark.ExportBookmarkSnapshotDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.bookmark.ManageBookmarkItemDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.bookmark.MoveBookmarkDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.importexport.ExportBookmarkMethodDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.importexport.ImportOptionDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.snapshot.BookmarkColorPickerDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.snapshot.DeleteBookmarkSnapshotDialog
import com.hdw.bookmarker.feature.home.presentation.dialog.snapshot.RenameBookmarkSnapshotDialog
import com.hdw.bookmarker.feature.home.presentation.model.HomeScreenUiState
import com.hdw.bookmarker.feature.home.presentation.model.HomeState

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
    onMoveBookmark: (SnapshotId, List<Int>, SnapshotId, List<Int>?) -> Unit,
    onDefaultBrowserSelected: (String) -> Unit,
    onBookmarkColorSelected: (SnapshotId, Long) -> Unit,
    onCopyBookmarkItem: (BookmarkItem) -> Unit,
    onPasteCopiedItem: (BookmarkItem, List<Int>?) -> Unit,
) {
    var showImportOptionDialog by uiState.showImportOptionDialog
    var showDefaultBrowserDialog by uiState.showDefaultBrowserDialog
    var showColorPickerDialog by uiState.showColorPickerDialog
    var pendingColorPickerSnapshotId by uiState.pendingColorPickerSnapshotId
    var showAddItemTypeDialog by uiState.showAddItemTypeDialog
    var showExportBookmarkMethodDialog by uiState.showExportBookmarkMethodDialog
    var pendingBookmarkExportAction by uiState.pendingBookmarkExportAction
    var pendingExportSnapshotId by uiState.pendingExportSnapshotId
    var showExportSnapshotDialog by uiState.showExportSnapshotDialog
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
    var pendingDeleteSnapshotTitle by uiState.pendingDeleteSnapshotTitle
    var pendingActionSnapshotId by uiState.pendingActionSnapshotId
    var pendingActionSnapshotTitle by uiState.pendingActionSnapshotTitle
    var pendingRenameSnapshotId by uiState.pendingRenameSnapshotId
    var pendingSnapshotTitle by uiState.pendingSnapshotTitle
    var pendingEditBookmarkItemPath by uiState.pendingEditBookmarkItemPath
    var pendingDeleteBookmarkItemPath by uiState.pendingDeleteBookmarkItemPath
    var pendingDeleteBookmarkItem by uiState.pendingDeleteBookmarkItem
    var pendingEditBookmarkItem by uiState.pendingEditBookmarkItem
    var pendingEditBookmarkTitle by uiState.pendingEditBookmarkTitle
    var pendingEditBookmarkUrl by uiState.pendingEditBookmarkUrl
    var pendingEditBookmarkTags by uiState.pendingEditBookmarkTags
    var pendingEditBookmarkDescription by uiState.pendingEditBookmarkDescription
    var pendingMoveBookmarkItem by uiState.pendingMoveBookmarkItem
    var pendingMoveBookmarkPath by uiState.pendingMoveBookmarkPath
    var pendingMoveSourceSnapshotId by uiState.pendingMoveSourceSnapshotId
    var pendingMoveTargetSnapshotId by uiState.pendingMoveTargetSnapshotId
    var showMoveBookmarkDialog by uiState.showMoveBookmarkDialog
    var showBookmarkItemActionDialog by uiState.showBookmarkItemActionDialog
    var showEditBookmarkItemDialog by uiState.showEditBookmarkItemDialog
    var copiedBookmarkItem by uiState.copiedBookmarkItem
    var pendingPasteFolderPath by uiState.pendingPasteFolderPath
    var showPasteActionDialog by uiState.showPasteActionDialog
    var showSnapshotActionDialog by uiState.showSnapshotActionDialog

    val colorTargetSnapshotId = pendingColorPickerSnapshotId ?: selectedBookmarkId
    if (showColorPickerDialog && colorTargetSnapshotId != null) {
        BookmarkColorPickerDialog(
            colors = BookmarkColorGenerator.getAllColors(),
            currentColor = state.bookmarkColors[colorTargetSnapshotId]
                ?: BookmarkColorGenerator.generateColorForId(colorTargetSnapshotId.value),
            onColorSelect = { color ->
                onBookmarkColorSelected(colorTargetSnapshotId, color)
            },
            onDismiss = {
                showColorPickerDialog = false
                pendingColorPickerSnapshotId = null
            },
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

    if (showMoveBookmarkDialog && pendingMoveSourceSnapshotId != null && pendingMoveBookmarkPath != null) {
        val sourceSnapshotId = pendingMoveSourceSnapshotId ?: return
        val moveTargets = state.bookmarkSnapshots.orderedIds
            .filter { snapshotId ->
                snapshotId != sourceSnapshotId && !state.bookmarkSnapshots.isInbox(snapshotId)
            }
            .mapNotNull { snapshotId ->
                state.bookmarkSnapshots[snapshotId]?.title
                    ?.trim()
                    ?.takeIf(String::isNotBlank)
                    ?.let { title -> snapshotId to title }
            }

        MoveBookmarkDialog(
            targets = moveTargets,
            onDismiss = {
                showMoveBookmarkDialog = false
                pendingMoveBookmarkItem = null
                pendingMoveBookmarkPath = null
                pendingMoveSourceSnapshotId = null
                pendingMoveTargetSnapshotId = null
            },
            onTargetClick = { targetSnapshotId ->
                val sourcePath = pendingMoveBookmarkPath ?: return@MoveBookmarkDialog
                val targetFolderPath = state.selectedFolderPaths.pathOf(targetSnapshotId)
                onMoveBookmark(
                    sourceSnapshotId,
                    sourcePath,
                    targetSnapshotId,
                    targetFolderPath,
                )
                showMoveBookmarkDialog = false
                pendingMoveBookmarkItem = null
                pendingMoveBookmarkPath = null
                pendingMoveSourceSnapshotId = null
                pendingMoveTargetSnapshotId = targetSnapshotId
            },
        )
    }

    if (showExportSnapshotDialog) {
        val exportTargets = state.bookmarkSnapshots.orderedIds
            .filterNot(state.bookmarkSnapshots::isInbox)
            .mapIndexedNotNull { index, snapshotId ->
                state.bookmarkSnapshots[snapshotId]?.let { snapshot ->
                    snapshotId to snapshot.title
                        .orEmpty()
                        .trim()
                        .ifBlank { "${stringResource(R.string.default_snapshot_title_prefix)}${index + 1}" }
                }
            }

        ExportBookmarkSnapshotDialog(
            targets = exportTargets,
            onDismiss = {
                showExportSnapshotDialog = false
                pendingExportSnapshotId = null
            },
            onTargetClick = { targetSnapshotId ->
                pendingExportSnapshotId = targetSnapshotId
                showExportSnapshotDialog = false
                showExportBookmarkMethodDialog = true
            },
        )
    }

    if (pendingDeleteSnapshotId != null) {
        DeleteBookmarkSnapshotDialog(
            snapshotTitle = pendingDeleteSnapshotTitle,
            onDismiss = {
                pendingDeleteSnapshotId = null
                pendingDeleteSnapshotTitle = ""
            },
            onConfirmDelete = {
                onDeleteBookmarkSnapshot(pendingDeleteSnapshotId ?: return@DeleteBookmarkSnapshotDialog)
                pendingDeleteSnapshotId = null
                pendingDeleteSnapshotTitle = ""
            },
        )
    }

    if (pendingRenameSnapshotId != null && pendingRenameSnapshotId !in state.bookmarkSnapshots.inboxIds) {
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
        val selectedSnapshotId = selectedBookmarkId
        val canMoveBookmark = selectedSnapshotId != null &&
            pendingEditBookmarkItem != null
        val itemActionTitle = when (val item = pendingEditBookmarkItem) {
            is BookmarkItem.Folder -> stringResource(
                R.string.bookmark_item_action_title_folder,
                item.title,
            )

            is BookmarkItem.Bookmark -> stringResource(
                R.string.bookmark_item_action_title_bookmark,
                item.title,
            )

            null -> null
        }
        BookmarkItemActionDialog(
            onDismiss = {
                showBookmarkItemActionDialog = false
                pendingEditBookmarkItemPath = null
                pendingEditBookmarkItem = null
            },
            titleText = itemActionTitle,
            onEditClick = {
                showBookmarkItemActionDialog = false
                showEditBookmarkItemDialog = true
            },
            onDeleteClick = {
                showBookmarkItemActionDialog = false
                pendingDeleteBookmarkItemPath = pendingEditBookmarkItemPath
                pendingDeleteBookmarkItem = pendingEditBookmarkItem
                pendingEditBookmarkItemPath = null
                pendingEditBookmarkItem = null
            },
            onCopyClick = {
                onCopyBookmarkItem(pendingEditBookmarkItem ?: return@BookmarkItemActionDialog)
                showBookmarkItemActionDialog = false
                pendingEditBookmarkItemPath = null
                pendingEditBookmarkItem = null
            },
            onPasteClick = {
                val copiedItem = copiedBookmarkItem ?: return@BookmarkItemActionDialog
                val targetFolderPath = pendingEditBookmarkItemPath ?: return@BookmarkItemActionDialog
                onPasteCopiedItem(copiedItem, targetFolderPath)
                showBookmarkItemActionDialog = false
                pendingEditBookmarkItemPath = null
                pendingEditBookmarkItem = null
            }.takeIf {
                copiedBookmarkItem != null &&
                    pendingEditBookmarkItem is BookmarkItem.Folder &&
                    selectedSnapshotId?.let(state.bookmarkSnapshots::isInbox) != true
            },
            onMoveClick = {
                val sourceSnapshotId = selectedSnapshotId ?: return@BookmarkItemActionDialog
                val item = pendingEditBookmarkItem ?: return@BookmarkItemActionDialog
                val itemPath = pendingEditBookmarkItemPath ?: return@BookmarkItemActionDialog
                uiState.pendingMoveSourceSnapshotId.value = sourceSnapshotId
                uiState.pendingMoveBookmarkItem.value = item
                uiState.pendingMoveBookmarkPath.value = itemPath
                uiState.pendingMoveTargetSnapshotId.value = null
                uiState.showMoveBookmarkDialog.value = true
                showBookmarkItemActionDialog = false
                pendingEditBookmarkItemPath = null
                pendingEditBookmarkItem = null
            }.takeIf { canMoveBookmark },
        )
    }

    if (showSnapshotActionDialog && pendingActionSnapshotId != null) {
        val snapshotId = pendingActionSnapshotId ?: return
        val isInboxSnapshot = state.bookmarkSnapshots.isInbox(snapshotId)
        BookmarkItemActionDialog(
            onDismiss = {
                showSnapshotActionDialog = false
                pendingActionSnapshotId = null
                pendingActionSnapshotTitle = ""
            },
            titleText = stringResource(
                R.string.bookmark_snapshot_action_title,
                pendingActionSnapshotTitle,
            ),
            onEditClick = if (isInboxSnapshot) {
                null
            } else {
                {
                    pendingRenameSnapshotId = snapshotId
                    pendingSnapshotTitle = pendingActionSnapshotTitle
                    showSnapshotActionDialog = false
                    pendingActionSnapshotId = null
                    pendingActionSnapshotTitle = ""
                }
            },
            onColorClick = {
                pendingColorPickerSnapshotId = snapshotId
                showColorPickerDialog = true
                showSnapshotActionDialog = false
                pendingActionSnapshotId = null
                pendingActionSnapshotTitle = ""
            },
            onDeleteClick = {
                pendingDeleteSnapshotId = snapshotId
                pendingDeleteSnapshotTitle = pendingActionSnapshotTitle
                showSnapshotActionDialog = false
                pendingActionSnapshotId = null
                pendingActionSnapshotTitle = ""
            },
        )
    }

    if (showPasteActionDialog && copiedBookmarkItem != null) {
        BookmarkItemActionDialog(
            onDismiss = {
                showPasteActionDialog = false
                pendingPasteFolderPath = null
            },
            onPasteClick = {
                val copiedItem = copiedBookmarkItem ?: return@BookmarkItemActionDialog
                onPasteCopiedItem(copiedItem, pendingPasteFolderPath)
                showPasteActionDialog = false
                pendingPasteFolderPath = null
            },
        )
    }

    if (pendingDeleteBookmarkItemPath != null && pendingDeleteBookmarkItem != null) {
        DeleteBookmarkItemDialog(
            title = when (val item = pendingDeleteBookmarkItem) {
                is BookmarkItem.Bookmark -> item.title
                is BookmarkItem.Folder -> item.title
                null -> ""
            },
            isFolder = pendingDeleteBookmarkItem is BookmarkItem.Folder,
            onDismiss = {
                pendingDeleteBookmarkItemPath = null
                pendingDeleteBookmarkItem = null
            },
            onConfirmDelete = {
                onDeleteBookmarkItem(pendingDeleteBookmarkItemPath ?: return@DeleteBookmarkItemDialog)
                pendingDeleteBookmarkItemPath = null
                pendingDeleteBookmarkItem = null
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
            onDismiss = {
                showExportBookmarkMethodDialog = false
                pendingExportSnapshotId = null
            },
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
