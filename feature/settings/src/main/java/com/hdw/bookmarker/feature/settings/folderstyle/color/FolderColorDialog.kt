package com.hdw.bookmarker.feature.settings.folderstyle.color

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
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconShape

@Composable
fun FolderColorDialog(
    selectedShape: BookmarkFolderIconShape,
    selectedColor: BookmarkFolderIconColor,
    onDismiss: () -> Unit,
    onColorSelect: (BookmarkFolderIconColor) -> Unit,
) {
    var pendingColor by remember(selectedColor) { mutableStateOf(selectedColor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.folder_color_label)) },
        text = {
            Column {
                BookmarkFolderIconColor.entries.forEach { color ->
                    FolderColorOptionRow(
                        color = color,
                        selectedShape = selectedShape,
                        isSelected = color == pendingColor,
                        onClick = { pendingColor = color },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onColorSelect(pendingColor) }) {
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
private fun FolderColorOptionRow(
    color: BookmarkFolderIconColor,
    selectedShape: BookmarkFolderIconShape,
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
            imageVector = selectedShape.iconVector(),
            contentDescription = null,
            tint = color.resolveTint(),
            modifier = Modifier
                .size(22.dp)
                .padding(end = 8.dp),
        )
        Text(
            text = color.label(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        RadioButton(selected = isSelected, onClick = onClick)
    }
}

@Preview(showBackground = true)
@Composable
private fun FolderColorDialogPreview() {
    MaterialTheme {
        FolderColorDialog(
            selectedShape = BookmarkFolderIconShape.FILLED,
            selectedColor = BookmarkFolderIconColor.DEFAULT,
            onDismiss = {},
            onColorSelect = {},
        )
    }
}
