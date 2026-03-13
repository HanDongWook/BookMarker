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
import com.hdw.bookmarker.core.ui.R

@Composable
fun AddFolderDialog(
    folderTitle: String,
    folderDescription: String,
    onFolderTitleChange: (String) -> Unit,
    onFolderDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.add_folder)) },
        text = {
            Column {
                OutlinedTextField(
                    value = folderTitle,
                    onValueChange = onFolderTitleChange,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.folder_name)) },
                )
                OutlinedTextField(
                    value = folderDescription,
                    onValueChange = onFolderDescriptionChange,
                    singleLine = false,
                    minLines = 2,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = { Text(text = stringResource(R.string.folder_description)) },
                )
            }
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

@Preview(showBackground = true)
@Composable
private fun AddFolderDialogPreview() {
    AddFolderDialog(
        folderTitle = "Reading",
        folderDescription = "Articles and references to revisit later.",
        onFolderTitleChange = {},
        onFolderDescriptionChange = {},
        onDismiss = {},
        onConfirm = {},
    )
}
