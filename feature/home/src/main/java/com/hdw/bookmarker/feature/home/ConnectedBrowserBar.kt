package com.hdw.bookmarker.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.hdw.bookmarker.core.model.browser.BrowserInfo

@Composable
fun ConnectedBrowserBar(
    installedBrowsers: List<BrowserInfo>,
    connectedBrowserPackages: Set<String>,
) {
    if (installedBrowsers.isEmpty()) return

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp,
            bottom = 8.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(installedBrowsers, key = { it.packageName }) { browser ->
            val isConnected = connectedBrowserPackages.contains(browser.packageName)
            Surface(
                color = if (isConnected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.Transparent
                },
                shape = MaterialTheme.shapes.small
            ) {
                Box(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Image(
                        painter = rememberDrawablePainter(drawable = browser.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .alpha(if (isConnected) 1f else 0.6f)
                    )
                }
            }
        }
    }

    HorizontalDivider()
}
