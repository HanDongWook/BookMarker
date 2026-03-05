package com.hdw.bookmarker.feature.home.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.R

@Composable
fun RenameBookmarkSnapshotDialog(
    snapshotTitle: String,
    onSnapshotTitleChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.rename_bookmark_snapshot)) },
        text = {
            OutlinedTextField(
                value = snapshotTitle,
                onValueChange = onSnapshotTitleChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.bookmark_snapshot_name)) },
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = snapshotTitle.isNotBlank(),
            ) {
                Text(text = stringResource(R.string.edit_mode_done))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun RenameBookmarkSnapshotDialogPreview() {
    RenameBookmarkSnapshotDialog(
        snapshotTitle = "북마크1",
        onSnapshotTitleChange = {},
        onDismiss = {},
        onConfirm = {},
    )
}
