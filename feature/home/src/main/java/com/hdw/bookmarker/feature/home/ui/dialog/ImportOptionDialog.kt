package com.hdw.bookmarker.feature.home.ui.dialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.ui.R

@Composable
fun ImportOptionDialog(
    onDismiss: () -> Unit,
    onOpenImportBookmarks: () -> Unit,
    onPickFile: () -> Unit,
    onAddEmptyBookmarkItem: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = onOpenImportBookmarks,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.import_bookmarks))
                }

                OutlinedButton(
                    onClick = onPickFile,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.import_option_dialog_pick_file))
                }

                OutlinedButton(
                    onClick = onAddEmptyBookmarkItem,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.import_option_dialog_add_empty_bookmark_item))
                }
            }
        },
        confirmButton = {},
        dismissButton = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun ImportOptionDialogPreview() {
    ImportOptionDialog(
        onDismiss = {},
        onOpenImportBookmarks = {},
        onPickFile = {},
        onAddEmptyBookmarkItem = {},
    )
}
