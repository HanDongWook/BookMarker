package com.hdw.bookmarker.feature.home.presentation.component.bookmark

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.hdw.bookmarker.feature.home.presentation.model.HomeScreenUiState

@Composable
internal fun HomeScreenBackHandler(uiState: HomeScreenUiState) {
    val shouldHandleBack = uiState.pendingDeleteBookmarkItemPath.value != null ||
        uiState.pendingEditBookmarkItemPath.value != null ||
        uiState.showAddBookmarkDialog.value ||
        uiState.showAddFolderDialog.value ||
        uiState.showExportBookmarkMethodDialog.value ||
        uiState.showAddItemTypeDialog.value ||
        uiState.showColorPickerDialog.value ||
        uiState.showSnapshotActionDialog.value ||
        uiState.showImportOptionDialog.value ||
        uiState.previewPaneState.currentUrl != null

    BackHandler(enabled = shouldHandleBack) {
        when {
            uiState.pendingDeleteBookmarkItemPath.value != null -> uiState.pendingDeleteBookmarkItemPath.value = null

            uiState.pendingEditBookmarkItemPath.value != null -> {
                uiState.pendingEditBookmarkItemPath.value = null
                uiState.pendingEditBookmarkItem.value = null
            }

            uiState.showAddBookmarkDialog.value -> {
                uiState.showAddBookmarkDialog.value = false
                uiState.addBookmarkToInbox.value = false
            }

            uiState.showAddFolderDialog.value -> uiState.showAddFolderDialog.value = false

            uiState.showExportBookmarkMethodDialog.value -> uiState.showExportBookmarkMethodDialog.value = false

            uiState.showAddItemTypeDialog.value -> uiState.showAddItemTypeDialog.value = false

            uiState.showColorPickerDialog.value -> {
                uiState.showColorPickerDialog.value = false
                uiState.pendingColorPickerSnapshotId.value = null
            }

            uiState.showSnapshotActionDialog.value -> {
                uiState.showSnapshotActionDialog.value = false
                uiState.pendingActionSnapshotId.value = null
                uiState.pendingActionSnapshotTitle.value = ""
            }

            uiState.showImportOptionDialog.value -> uiState.showImportOptionDialog.value = false

            uiState.previewPaneState.currentUrl != null -> uiState.previewPaneState.clear()
        }
    }
}
