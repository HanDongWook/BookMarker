package com.hdw.bookmarker.feature.home.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.ui.R

@Composable
fun AddBookmarkDialog(
    bookmarkTitle: String,
    bookmarkUrl: String,
    onBookmarkTitleChange: (String) -> Unit,
    onBookmarkUrlChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.add_bookmark)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = bookmarkTitle,
                    onValueChange = onBookmarkTitleChange,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.bookmark_name)) },
                )
                OutlinedTextField(
                    value = bookmarkUrl,
                    onValueChange = onBookmarkUrlChange,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.bookmark_url)) },
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = bookmarkTitle.isNotBlank() && bookmarkUrl.isNotBlank(),
            ) {
                Text(text = stringResource(R.string.add))
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
private fun AddBookmarkDialogPreview() {
    AddBookmarkDialog(
        bookmarkTitle = "GitHub",
        bookmarkUrl = "https://github.com",
        onBookmarkTitleChange = {},
        onBookmarkUrlChange = {},
        onDismiss = {},
        onConfirm = {},
    )
}
