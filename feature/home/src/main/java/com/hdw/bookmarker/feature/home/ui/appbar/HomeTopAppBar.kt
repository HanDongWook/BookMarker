package com.hdw.bookmarker.feature.home.ui.appbar
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.home.contract.BookmarkDisplayType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    bookmarkDisplayType: BookmarkDisplayType,
    defaultBrowserIcon: Drawable?,
    onSearchClick: () -> Unit,
    onBookmarkDisplayTypeClick: () -> Unit,
    onDefaultBrowserIconClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                stringResource(R.string.title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.bookmark_search_open),
                )
            }
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
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeTopAppBarPreview() {
    MaterialTheme {
        HomeTopAppBar(
            bookmarkDisplayType = BookmarkDisplayType.LIST,
            defaultBrowserIcon = null,
            onSearchClick = {},
            onBookmarkDisplayTypeClick = {},
            onDefaultBrowserIconClick = {},
            onSettingsClick = {},
        )
    }
}
