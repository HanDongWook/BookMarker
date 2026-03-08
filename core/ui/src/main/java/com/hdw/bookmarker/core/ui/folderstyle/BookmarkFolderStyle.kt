package com.hdw.bookmarker.core.ui.folderstyle

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.ui.R

data class BookmarkFolderIconStyle(
    val shape: BookmarkFolderIconShape = BookmarkFolderIconShape.FILLED,
    val color: BookmarkFolderIconColor = BookmarkFolderIconColor.DEFAULT,
)

enum class BookmarkFolderIconShape {
    FILLED,
    OUTLINED,
    ;

    fun iconVector(): ImageVector = when (this) {
        FILLED -> Icons.Filled.Folder
        OUTLINED -> Icons.Outlined.Folder
    }

    @Composable
    fun label(): String = when (this) {
        FILLED -> stringResource(R.string.folder_shape_filled)
        OUTLINED -> stringResource(R.string.folder_shape_outlined)
    }

    companion object {
        fun fromPersisted(value: String?): BookmarkFolderIconShape = entries.firstOrNull { it.name == value } ?: FILLED
    }
}

enum class BookmarkFolderIconColor {
    DEFAULT,
    BLUE,
    GREEN,
    ORANGE,
    PURPLE,
    ;

    @Composable
    fun resolveTint(): Color = when (this) {
        DEFAULT -> MaterialTheme.colorScheme.primary
        BLUE -> Color(0xFF1976D2)
        GREEN -> Color(0xFF2E7D32)
        ORANGE -> Color(0xFFEF6C00)
        PURPLE -> Color(0xFF7B1FA2)
    }

    @Composable
    fun label(): String = when (this) {
        DEFAULT -> stringResource(R.string.folder_color_default)
        BLUE -> stringResource(R.string.folder_color_blue)
        GREEN -> stringResource(R.string.folder_color_green)
        ORANGE -> stringResource(R.string.folder_color_orange)
        PURPLE -> stringResource(R.string.folder_color_purple)
    }

    companion object {
        fun fromPersisted(value: String?): BookmarkFolderIconColor = entries.firstOrNull { it.name == value } ?: DEFAULT
    }
}
