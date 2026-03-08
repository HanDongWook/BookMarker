package com.hdw.bookmarker.feature.home.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hdw.bookmarker.core.ui.R

@Composable
fun AddItemTypeDialog(
    onDismiss: () -> Unit,
    onAddFolderClick: () -> Unit,
    onAddBookmarkClick: () -> Unit,
    onShareClick: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.add_bookmark_or_folder),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    AddItemTypeOption(
                        modifier = Modifier.weight(1f),
                        imageVector = Icons.Default.CreateNewFolder,
                        title = stringResource(R.string.add_folder),
                        contentDescription = stringResource(R.string.add_folder),
                        onClick = onAddFolderClick,
                    )
                    AddItemTypeOption(
                        modifier = Modifier.weight(1f),
                        imageVector = Icons.Default.BookmarkAdd,
                        title = stringResource(R.string.add_bookmark),
                        contentDescription = stringResource(R.string.add_bookmark),
                        onClick = onAddBookmarkClick,
                    )
                    AddItemTypeOption(
                        modifier = Modifier.weight(1f),
                        imageVector = Icons.Default.Share,
                        title = stringResource(R.string.share_current_bookmarks_label),
                        contentDescription = stringResource(R.string.share_current_bookmarks_label),
                        onClick = onShareClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun AddItemTypeOption(
    imageVector: ImageVector,
    title: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(40.dp),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
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
