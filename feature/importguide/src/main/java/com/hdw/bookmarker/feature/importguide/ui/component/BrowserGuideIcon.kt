package com.hdw.bookmarker.feature.importguide.ui.component

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.hdw.bookmarker.core.model.browser.Browser

@Composable
fun BrowserGuideIcon(
    browser: Browser,
    displayName: String,
    installedIcon: Drawable?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
) {
    if (installedIcon != null) {
        Image(
            painter = rememberDrawablePainter(drawable = installedIcon),
            contentDescription = displayName,
            modifier = modifier.size(size),
        )
        return
    }

    Surface(
        modifier = modifier.size(size),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Public,
                contentDescription = displayName,
                modifier = Modifier.size(size * 0.56f),
            )
        }
    }
}
