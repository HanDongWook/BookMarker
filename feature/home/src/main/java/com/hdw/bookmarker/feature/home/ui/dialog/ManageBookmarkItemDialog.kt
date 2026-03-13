package com.hdw.bookmarker.feature.home.ui.dialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.ui.R

@Composable
fun ManageBookmarkItemDialog(
    item: BookmarkItem,
    title: String,
    url: String,
    note: String,
    tags: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onUrlChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onTagsChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onDelete: () -> Unit,
) {
    val canApply = title.isNotBlank() && (item !is BookmarkItem.Bookmark || url.isNotBlank())
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.edit_mode_editing)) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    label = {
                        Text(
                            text = when (item) {
                                is BookmarkItem.Folder -> stringResource(R.string.folder_name)
                                is BookmarkItem.Bookmark -> stringResource(R.string.bookmark_name)
                            },
                        )
                    },
                )
                if (item is BookmarkItem.Bookmark) {
                    OutlinedTextField(
                        value = url,
                        onValueChange = onUrlChange,
                        singleLine = false,
                        minLines = 2,
                        maxLines = 10,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = { Text(text = stringResource(R.string.bookmark_url)) },
                    )
                    OutlinedTextField(
                        value = tags,
                        onValueChange = onTagsChange,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = { Text(text = stringResource(R.string.bookmark_tags)) },
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = onNoteChange,
                        singleLine = false,
                        minLines = 2,
                        maxLines = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = { Text(text = stringResource(R.string.bookmark_note)) },
                    )
                } else {
                    OutlinedTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        singleLine = false,
                        minLines = 2,
                        maxLines = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = { Text(text = stringResource(R.string.folder_description)) },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onApply, enabled = canApply) {
                Text(text = stringResource(R.string.action_apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDelete) {
                Text(text = stringResource(R.string.delete_bookmark_item_dialog_confirm))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun ManageFolderDialogPreview() {
    ManageBookmarkItemDialog(
        item = BookmarkItem.Folder(
            title = "개발",
            description = "개발 관련 링크 모음",
            addDate = null,
            lastModified = null,
            children = emptyList(),
        ),
        title = "개발",
        url = "",
        note = "",
        tags = "",
        description = "개발 관련 링크 모음",
        onTitleChange = {},
        onUrlChange = {},
        onNoteChange = {},
        onTagsChange = {},
        onDescriptionChange = {},
        onDismiss = {},
        onApply = {},
        onDelete = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun ManageBookmarkDialogPreview() {
    ManageBookmarkItemDialog(
        item = BookmarkItem.Bookmark(
            title = "OpenAI",
            url = "https://openai.com",
            addDate = null,
            lastModified = null,
            iconUri = null,
        ),
        title = "OpenAI",
        url = "https://openai.com",
        note = "LLM docs",
        tags = "ai, docs",
        description = "",
        onTitleChange = {},
        onUrlChange = {},
        onNoteChange = {},
        onTagsChange = {},
        onDescriptionChange = {},
        onDismiss = {},
        onApply = {},
        onDelete = {},
    )
}
