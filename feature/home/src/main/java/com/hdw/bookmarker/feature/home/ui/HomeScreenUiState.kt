package com.hdw.bookmarker.feature.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.feature.home.ui.export.BookmarkExportAction
import com.hdw.bookmarker.feature.home.ui.preview.BookmarkPreviewPaneState
import com.hdw.bookmarker.feature.home.ui.preview.rememberBookmarkPreviewPaneState

class HomeScreenUiState(
    val previewPaneState: BookmarkPreviewPaneState,
    val showImportOptionDialog: MutableState<Boolean>,
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
    val pendingBookmarkNote: MutableState<String>,
    val pendingBookmarkTags: MutableState<String>,
    val addBookmarkToInbox: MutableState<Boolean>,
    val pendingDeleteSnapshotId: MutableState<SnapshotId?>,
    val pendingRenameSnapshotId: MutableState<SnapshotId?>,
    val pendingSnapshotTitle: MutableState<String>,
    val pendingEditBookmarkItemPath: MutableState<List<Int>?>,
    val pendingDeleteBookmarkItemPath: MutableState<List<Int>?>,
    val pendingEditBookmarkItem: MutableState<BookmarkItem?>,
    val pendingEditBookmarkTitle: MutableState<String>,
    val pendingEditBookmarkUrl: MutableState<String>,
    val pendingEditBookmarkNote: MutableState<String>,
    val pendingEditBookmarkTags: MutableState<String>,
    val pendingEditBookmarkDescription: MutableState<String>,
    val showSearchDialog: MutableState<Boolean>,
    val searchQuery: MutableState<String>,
    val isBrowserEditMode: MutableState<Boolean>,
)

@Composable
fun rememberHomeScreenUiState(
    previewPaneState: BookmarkPreviewPaneState = rememberBookmarkPreviewPaneState(),
): HomeScreenUiState = remember(previewPaneState) {
    HomeScreenUiState(
        previewPaneState = previewPaneState,
        showImportOptionDialog = mutableStateOf(false),
        showDefaultBrowserDialog = mutableStateOf(false),
        showColorPickerDialog = mutableStateOf(false),
        showAddItemTypeDialog = mutableStateOf(false),
        showExportBookmarkMethodDialog = mutableStateOf(false),
        pendingBookmarkExportAction = mutableStateOf(null),
        showAddFolderDialog = mutableStateOf(false),
        showAddBookmarkDialog = mutableStateOf(false),
        pendingFolderTitle = mutableStateOf(""),
        pendingFolderDescription = mutableStateOf(""),
        pendingBookmarkTitle = mutableStateOf(""),
        pendingBookmarkUrl = mutableStateOf(""),
        pendingBookmarkNote = mutableStateOf(""),
        pendingBookmarkTags = mutableStateOf(""),
        addBookmarkToInbox = mutableStateOf(false),
        pendingDeleteSnapshotId = mutableStateOf(null),
        pendingRenameSnapshotId = mutableStateOf(null),
        pendingSnapshotTitle = mutableStateOf(""),
        pendingEditBookmarkItemPath = mutableStateOf(null),
        pendingDeleteBookmarkItemPath = mutableStateOf(null),
        pendingEditBookmarkItem = mutableStateOf(null),
        pendingEditBookmarkTitle = mutableStateOf(""),
        pendingEditBookmarkUrl = mutableStateOf(""),
        pendingEditBookmarkNote = mutableStateOf(""),
        pendingEditBookmarkTags = mutableStateOf(""),
        pendingEditBookmarkDescription = mutableStateOf(""),
        showSearchDialog = mutableStateOf(false),
        searchQuery = mutableStateOf(""),
        isBrowserEditMode = mutableStateOf(false),
    )
}
