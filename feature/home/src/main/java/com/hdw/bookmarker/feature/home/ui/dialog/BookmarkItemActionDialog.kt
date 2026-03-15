package com.hdw.bookmarker.feature.home.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
fun BookmarkItemActionDialog(
    onDismiss: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onCopyClick: (() -> Unit)? = null,
    onPasteClick: (() -> Unit)? = null,
) {
    val actions = buildList {
        if (onEditClick != null) {
            add(
                BookmarkItemAction(
                    imageVector = Icons.Default.Edit,
                    title = R.string.bookmark_item_action_edit,
                    onClick = onEditClick,
                ),
            )
        }
        if (onDeleteClick != null) {
            add(
                BookmarkItemAction(
                    imageVector = Icons.Default.Delete,
                    title = R.string.bookmark_item_action_delete,
                    onClick = onDeleteClick,
                ),
            )
        }
        if (onCopyClick != null) {
            add(
                BookmarkItemAction(
                    imageVector = Icons.Default.ContentCopy,
                    title = R.string.bookmark_item_action_copy,
                    onClick = onCopyClick,
                ),
            )
        }
        if (onPasteClick != null) {
            add(
                BookmarkItemAction(
                    imageVector = Icons.Default.ContentPaste,
                    title = R.string.bookmark_item_action_paste,
                    onClick = onPasteClick,
                ),
            )
        }
    }
    if (actions.isEmpty()) return

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
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    actions.forEach { action ->
                        BookmarkItemActionOption(
                            modifier = Modifier.weight(1f),
                            imageVector = action.imageVector,
                            title = stringResource(action.title),
                            onClick = action.onClick,
                        )
                    }
                }
            }
        }
    }
}

private data class BookmarkItemAction(val imageVector: ImageVector, val title: Int, val onClick: () -> Unit)

@Composable
private fun BookmarkItemActionOption(
    imageVector: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkItemActionDialogPreview() {
    BookmarkItemActionDialog(
        onDismiss = {},
        onEditClick = {},
        onDeleteClick = {},
        onCopyClick = {},
    )
}
