package com.hdw.bookmarker.feature.home

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.hdw.bookmarker.core.model.browser.BrowserInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ConnectedBrowserBar(
    installedBrowsers: List<BrowserInfo>,
    connectedBrowserPackages: Set<String>,
    selectedBrowserPackage: String?,
    isEditMode: Boolean,
    onBrowserClick: (String) -> Unit,
    onEnterEditMode: () -> Unit,
    onDeleteRequest: (String) -> Unit,
) {
    val connectedBrowsers = installedBrowsers.filter { browser ->
        connectedBrowserPackages.contains(browser.packageName)
    }
    if (connectedBrowsers.isEmpty()) return

    val shakeRotation = rememberInfiniteTransition(label = "connected_browser_shake").animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 120),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "connected_browser_shake_rotation",
    )

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
                        .combinedClickable(
                            onClick = { onBrowserClick(browser.packageName) },
                            onLongClick = onEnterEditMode,
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Image(
                        painter = rememberDrawablePainter(drawable = browser.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .graphicsLayer {
                                rotationZ = if (isEditMode) shakeRotation.value else 0f
                            }
                            .alpha(1f),
                    )

                    if (isEditMode) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(16.dp),
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.error,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .combinedClickable(
                                        onClick = { onDeleteRequest(browser.packageName) },
                                        onLongClick = {},
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onError,
                                    modifier = Modifier.size(10.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    HorizontalDivider()
}
