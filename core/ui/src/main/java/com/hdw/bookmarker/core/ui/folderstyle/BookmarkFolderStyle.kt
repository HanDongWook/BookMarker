package com.hdw.bookmarker.core.ui.folderstyle

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.core.ui.R

fun BookmarkFolderIconShape.iconVector(): ImageVector = when (this) {
    BookmarkFolderIconShape.FILLED -> Icons.Filled.Folder
    BookmarkFolderIconShape.OUTLINED -> Icons.Outlined.Folder
}

@Composable
fun BookmarkFolderIconShape.label(): String = when (this) {
    BookmarkFolderIconShape.FILLED -> stringResource(R.string.folder_shape_filled)
    BookmarkFolderIconShape.OUTLINED -> stringResource(R.string.folder_shape_outlined)
}

@Composable
fun BookmarkFolderIconColor.resolveTint(): Color = when (this) {
    BookmarkFolderIconColor.DEFAULT -> MaterialTheme.colorScheme.primary
    BookmarkFolderIconColor.BLUE -> Color(0xFF1976D2)
    BookmarkFolderIconColor.GREEN -> Color(0xFF2E7D32)
    BookmarkFolderIconColor.ORANGE -> Color(0xFFEF6C00)
    BookmarkFolderIconColor.PURPLE -> Color(0xFF7B1FA2)
}

@Composable
fun BookmarkFolderIconColor.label(): String = when (this) {
    BookmarkFolderIconColor.DEFAULT -> stringResource(R.string.folder_color_default)
    BookmarkFolderIconColor.BLUE -> stringResource(R.string.folder_color_blue)
    BookmarkFolderIconColor.GREEN -> stringResource(R.string.folder_color_green)
    BookmarkFolderIconColor.ORANGE -> stringResource(R.string.folder_color_orange)
    BookmarkFolderIconColor.PURPLE -> stringResource(R.string.folder_color_purple)
}
