package com.hdw.bookmarker.core.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.hdw.bookmarker.core.ui.R

@Composable
internal fun AppBottomBar(
    modifier: Modifier = Modifier,
    backdrop: LayerBackdrop,
    currentRoute: AppRoute?,
    onBookmarksClick: () -> Unit,
    onTrendsClick: () -> Unit,
) {
    val barShape = MaterialTheme.shapes.extraLarge
    val glassSurfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
    val indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)

    NavigationBar(
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { barShape },
                effects = {
                    vibrancy()
                    blur(4.dp.toPx())
                    lens(16.dp.toPx(), 32.dp.toPx())
                },
                onDrawSurface = {
                    drawRect(glassSurfaceColor)
                },
            ),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        NavigationBarItem(
            selected = currentRoute == AppRoute.Bookmarks,
            onClick = onBookmarksClick,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = indicatorColor,
            ),
            icon = {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = stringResource(R.string.bottom_tab_bookmarks),
                )
            },
            label = { Text(text = stringResource(R.string.bottom_tab_bookmarks)) },
        )
        NavigationBarItem(
            selected = currentRoute == AppRoute.Trends,
            onClick = onTrendsClick,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = indicatorColor,
            ),
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = stringResource(R.string.bottom_tab_trends),
                )
            },
            label = { Text(text = stringResource(R.string.bottom_tab_trends)) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppBottomBarPreview() {
    MaterialTheme {
        val previewSurfaceColor = MaterialTheme.colorScheme.surface
        val previewBackdrop = rememberLayerBackdrop {
            drawRect(previewSurfaceColor)
            drawContent()
        }
        AppBottomBar(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
                .height(72.dp),
            backdrop = previewBackdrop,
            currentRoute = AppRoute.Bookmarks,
            onBookmarksClick = {},
            onTrendsClick = {},
        )
    }
}
