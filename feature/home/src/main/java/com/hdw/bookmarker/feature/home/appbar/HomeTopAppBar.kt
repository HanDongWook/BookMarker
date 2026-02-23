package com.hdw.bookmarker.feature.home.appbar

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.hdw.bookmarker.feature.home.R
import com.hdw.bookmarker.feature.home.boomarkcontent.BookmarkDisplayType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    isEditMode: Boolean,
    bookmarkDisplayType: BookmarkDisplayType,
    defaultBrowserIcon: Drawable?,
    onBookmarkDisplayTypeClick: () -> Unit,
    onDefaultBrowserIconClick: () -> Unit,
    onMenuClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditModeDoneClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.title)) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.menu_description),
                )
            }
        },
        actions = {
            if (isEditMode) {
                TextButton(onClick = onEditModeDoneClick) {
                    Text(text = stringResource(R.string.edit_mode_done))
                }
            } else {
                IconButton(onClick = onBookmarkDisplayTypeClick) {
                    Icon(
                        imageVector = when (bookmarkDisplayType) {
                            BookmarkDisplayType.LIST -> Icons.AutoMirrored.Filled.ViewList
                            BookmarkDisplayType.ICON -> Icons.Default.Apps
                        },
                        contentDescription = when (bookmarkDisplayType) {
                            BookmarkDisplayType.LIST -> stringResource(R.string.bookmark_mode_list)
                            BookmarkDisplayType.ICON -> stringResource(R.string.bookmark_mode_icon)
                        },
                    )
                }
                if (defaultBrowserIcon != null) {
                    IconButton(onClick = onDefaultBrowserIconClick) {
                        Image(
                            painter = rememberDrawablePainter(drawable = defaultBrowserIcon),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.menu_settings),
                    )
                }
            }
        },
    )
}
