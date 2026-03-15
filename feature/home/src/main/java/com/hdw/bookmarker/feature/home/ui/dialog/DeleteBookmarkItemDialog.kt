package com.hdw.bookmarker.feature.home.ui.dialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.R
import timber.log.Timber

@Composable
fun DeleteBookmarkItemDialog(
    title: String,
    isFolder: Boolean,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
) {
    val text = stringResource(
        if (isFolder) {
            R.string.delete_bookmark_item_dialog_message_folder
        } else {
            R.string.delete_bookmark_item_dialog_message_bookmark
        },
        title,
    )
    Timber.e("DeleteBookmarkItemDialog: $text")
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.delete_bookmark_item_dialog_title)) },
        text = {
            Text(
                text = text,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmDelete) {
                Text(text = stringResource(R.string.delete_bookmark_item_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.delete_bookmark_item_dialog_cancel))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteBookmarkItemDialogPreview() {
    DeleteBookmarkItemDialog(
        title = "샘플",
        isFolder = true,
        onDismiss = {},
        onConfirmDelete = {},
    )
}
