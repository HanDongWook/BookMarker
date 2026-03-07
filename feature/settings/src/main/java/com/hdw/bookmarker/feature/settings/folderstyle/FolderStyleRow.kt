package com.hdw.bookmarker.feature.settings.folderstyle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.feature.settings.SettingsRow

@Composable
fun FolderStyleRow(shape: BookmarkFolderIconShape, color: BookmarkFolderIconColor, onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.folder_style_label),
        onClick = onClick,
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = shape.iconVector(),
                    contentDescription = null,
                    tint = color.resolveTint(),
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "${shape.label()} / ${color.label()}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun FolderStyleRowPreview() {
    MaterialTheme {
        FolderStyleRow(
            shape = BookmarkFolderIconShape.FILLED,
            color = BookmarkFolderIconColor.DEFAULT,
            onClick = {},
        )
    }
}
