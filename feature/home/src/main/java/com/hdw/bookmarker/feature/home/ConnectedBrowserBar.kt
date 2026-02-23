package com.hdw.bookmarker.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
internal fun ConnectedBrowserBar(
    installedBrowsers: List<BrowserInfo>,
    connectedBrowserPackages: Set<String>,
    selectedBrowserPackage: String?,
    onBrowserClick: (String) -> Unit,
) {
    val connectedBrowsers = installedBrowsers.filter { browser ->
        connectedBrowserPackages.contains(browser.packageName)
    }
    if (connectedBrowsers.isEmpty()) return

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp,
            bottom = 8.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(connectedBrowsers, key = { it.packageName }) { browser ->
            val isSelected = selectedBrowserPackage == browser.packageName
            Surface(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.Transparent
                },
                shape = MaterialTheme.shapes.small,
            ) {
                Box(
                    modifier = Modifier
                        .clickable { onBrowserClick(browser.packageName) }
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Image(
                        painter = rememberDrawablePainter(drawable = browser.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .alpha(1f),
                    )
                }
            }
        }
    }

    HorizontalDivider()
}
