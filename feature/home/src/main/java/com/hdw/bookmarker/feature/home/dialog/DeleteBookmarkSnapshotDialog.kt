package com.hdw.bookmarker.feature.home.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.R

@Composable
fun DeleteBookmarkSnapshotDialog(onDismiss: () -> Unit, onConfirmDelete: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.delete_bookmark_dialog_title)) },
        text = { Text(text = stringResource(R.string.delete_bookmark_dialog_message)) },
        confirmButton = {
            TextButton(onClick = onConfirmDelete) {
                Text(text = stringResource(R.string.delete_bookmark_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.delete_bookmark_dialog_cancel))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteBookmarkSnapshotDialogPreview() {
    DeleteBookmarkSnapshotDialog(
        onDismiss = {},
        onConfirmDelete = {},
    )
}
