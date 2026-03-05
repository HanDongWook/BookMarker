package com.hdw.bookmarker.feature.home.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.ui.R

@Composable
fun AddItemTypeDialog(
    onDismiss: () -> Unit,
    onAddFolderClick: () -> Unit,
    onAddBookmarkClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onAddFolderClick) {
                        Icon(
                            imageVector = Icons.Default.CreateNewFolder,
                            contentDescription = stringResource(R.string.add_folder),
                            modifier = Modifier.size(60.dp),
                        )
                    }

                    IconButton(onClick = onAddBookmarkClick) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdd,
                            contentDescription = stringResource(R.string.add_bookmark),
                            modifier = Modifier.size(60.dp),
                        )
                    }

                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share_current_bookmarks_label),
                            modifier = Modifier.size(60.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {},
    )
}

@Composable
fun AddFolderDialog(
    folderTitle: String,
    onFolderTitleChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.add_folder)) },
        text = {
            OutlinedTextField(
                value = folderTitle,
                onValueChange = onFolderTitleChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.folder_name)) },
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = folderTitle.isNotBlank(),
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
private fun AddItemTypeDialogPreview() {
    AddItemTypeDialog(
        onDismiss = {},
        onAddFolderClick = {},
        onAddBookmarkClick = {},
        onShareClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun AddFolderDialogPreview() {
    AddFolderDialog(
        folderTitle = "Reading",
        onFolderTitleChange = {},
        onDismiss = {},
        onConfirm = {},
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
