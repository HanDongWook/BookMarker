package com.hdw.bookmarker.feature.settings.ui.tab.folderstyle.shape

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.iconVector
import com.hdw.bookmarker.core.ui.folderstyle.label
import com.hdw.bookmarker.core.ui.folderstyle.resolveTint

@Composable
fun FolderShapeDialog(
    selectedFolderIconStyle: BookmarkFolderIconStyle,
    onDismiss: () -> Unit,
    onShapeSelect: (BookmarkFolderIconShape) -> Unit,
) {
    val selectedShape = selectedFolderIconStyle.shape
    var pendingShape by remember(selectedShape) { mutableStateOf(selectedShape) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.folder_shape_label)) },
        text = {
            Column {
                BookmarkFolderIconShape.entries.forEach { shape ->
                    FolderShapeOptionRow(
                        shape = shape,
                        selectedFolderIconStyle = selectedFolderIconStyle,
                        isSelected = shape == pendingShape,
                        onClick = { pendingShape = shape },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onShapeSelect(pendingShape) }) {
                Text(text = stringResource(android.R.string.ok))
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
private fun FolderShapeOptionRow(
    shape: BookmarkFolderIconShape,
    selectedFolderIconStyle: BookmarkFolderIconStyle,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = shape.iconVector(),
            contentDescription = null,
            tint = selectedFolderIconStyle.color.resolveTint(),
            modifier = Modifier
                .size(22.dp)
                .padding(end = 8.dp),
        )
        Text(
            text = shape.label(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        RadioButton(selected = isSelected, onClick = onClick)
    }
}

@Preview(showBackground = true)
@Composable
private fun FolderShapeDialogPreview() {
    MaterialTheme {
        FolderShapeDialog(
            selectedFolderIconStyle = BookmarkFolderIconStyle(),
            onDismiss = {},
            onShapeSelect = {},
        )
    }
}
