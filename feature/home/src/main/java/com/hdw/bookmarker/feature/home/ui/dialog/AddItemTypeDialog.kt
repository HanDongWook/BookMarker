package com.hdw.bookmarker.feature.home.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
fun AddItemTypeDialog(onDismiss: () -> Unit, onAddFolderClick: () -> Unit, onAddBookmarkClick: () -> Unit) {
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

@Preview(showBackground = true)
@Composable
private fun AddItemTypeDialogPreview() {
    AddItemTypeDialog(
        onDismiss = {},
        onAddFolderClick = {},
        onAddBookmarkClick = {},
    )
}
