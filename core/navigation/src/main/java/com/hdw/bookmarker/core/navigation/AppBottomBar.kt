package com.hdw.bookmarker.core.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.hdw.bookmarker.core.ui.R
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

private const val BottomBarResizeAnimationDurationMillis = 220
private const val BottomBarAddButtonAnimationDurationMillis = 140

@Composable
internal fun AppBottomBar(
    modifier: Modifier = Modifier,
    backdrop: LayerBackdrop,
    currentRoute: AppRoute?,
    onBookmarksClick: () -> Unit,
    onTrendsClick: () -> Unit,
    showAddButton: Boolean = false,
    onAddClick: (() -> Unit)? = null,
) {
    val showAddButtonSlot = showAddButton && onAddClick != null
    val barShape = MaterialTheme.shapes.extraLarge
    val glassSurfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
    val indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
    val addButtonSurfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
    val addButtonContentColor = MaterialTheme.colorScheme.onSurface
    val addButtonSlotWidth = 54.dp
    val addButtonSize = 54.dp
    val expandedSpacing = 16.dp
    val slotProgress = remember { Animatable(if (showAddButtonSlot) 1f else 0f) }
    val buttonProgress = remember { Animatable(if (showAddButtonSlot) 1f else 0f) }
    var hasAnimatedOnce by remember { mutableStateOf(false) }

    LaunchedEffect(showAddButtonSlot) {
        if (!hasAnimatedOnce) {
            slotProgress.snapTo(if (showAddButtonSlot) 1f else 0f)
            buttonProgress.snapTo(if (showAddButtonSlot) 1f else 0f)
            hasAnimatedOnce = true
            return@LaunchedEffect
        }

        if (showAddButtonSlot) {
            slotProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = BottomBarResizeAnimationDurationMillis),
            )
            buttonProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = BottomBarAddButtonAnimationDurationMillis),
            )
        } else {
            buttonProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = BottomBarAddButtonAnimationDurationMillis),
            )
            slotProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = BottomBarResizeAnimationDurationMillis),
            )
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            lerp(0.dp, expandedSpacing, slotProgress.value),
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavigationBar(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
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

        Box(
            modifier = Modifier
                .width(lerp(0.dp, addButtonSlotWidth, slotProgress.value))
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            FloatingActionButton(
                onClick = { onAddClick?.invoke() },
                shape = CircleShape,
                containerColor = Color.Transparent,
                contentColor = addButtonContentColor,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    focusedElevation = 0.dp,
                    hoveredElevation = 0.dp,
                ),
                modifier = Modifier
                    .size(addButtonSize)
                    .clip(CircleShape)
                    .graphicsLayer {
                        alpha = buttonProgress.value
                        scaleX = 0.85f + (0.15f * buttonProgress.value)
                        scaleY = 0.85f + (0.15f * buttonProgress.value)
                    }
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { CircleShape },
                        effects = {
                            vibrancy()
                            blur(4.dp.toPx())
                            lens(16.dp.toPx(), 32.dp.toPx())
                        },
                        onDrawSurface = {
                            drawRect(addButtonSurfaceColor)
                        },
                    ),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_bookmark_or_folder),
                )
            }
        }
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
            showAddButton = true,
            onAddClick = {},
        )
    }
}
