package com.hdw.bookmarker.core.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.ui.R
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

@Composable
internal fun HomeBottomBar(
    modifier: Modifier = Modifier,
    backdrop: LayerBackdrop,
    currentRoute: AppRoute?,
    onBookmarksClick: () -> Unit,
    onTrendsClick: () -> Unit,
    onAddClick: () -> Unit,
) {
    val addButtonTint = MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
            .height(72.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppBottomBar(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            backdrop = backdrop,
            currentRoute = currentRoute,
            onBookmarksClick = onBookmarksClick,
            onTrendsClick = onTrendsClick,
        )
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(0.75f)
        ) {
            val diameter = if (maxWidth < maxHeight) maxWidth else maxHeight

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                FloatingActionButton(
                    onClick = onAddClick,
                    shape = CircleShape,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        focusedElevation = 0.dp,
                        hoveredElevation = 0.dp,
                    ),
                    modifier = Modifier
                        .size(diameter)
                        .drawBackdrop(
                            backdrop = backdrop,
                            shape = { CircleShape },
                            effects = {
                                vibrancy()
                                blur(4.dp.toPx())
                                lens(16.dp.toPx(), 32.dp.toPx())
                            },
                            onDrawSurface = {
                                drawRect(addButtonTint, blendMode = BlendMode.Hue)
                                drawRect(addButtonTint.copy(alpha = 0.82f))
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
}
