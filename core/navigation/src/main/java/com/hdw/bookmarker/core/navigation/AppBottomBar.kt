package com.hdw.bookmarker.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.hdw.bookmarker.core.ui.R

@Composable
internal fun AppBottomBar(
    currentDestination: NavDestination?,
    onBookmarksClick: () -> Unit,
    onTrendsClick: () -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentDestination?.hasRoute<AppRoute.Bookmarks>() == true,
            onClick = onBookmarksClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = stringResource(R.string.bottom_tab_bookmarks),
                )
            },
            label = { Text(text = stringResource(R.string.bottom_tab_bookmarks)) },
        )
        NavigationBarItem(
            selected = currentDestination?.hasRoute<AppRoute.Trends>() == true,
            onClick = onTrendsClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = stringResource(R.string.bottom_tab_trends),
                )
            },
            label = { Text(text = stringResource(R.string.bottom_tab_trends)) },
        )
    }
}
