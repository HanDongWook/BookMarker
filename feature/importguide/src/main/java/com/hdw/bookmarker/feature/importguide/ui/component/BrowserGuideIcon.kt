package com.hdw.bookmarker.feature.importguide.ui.component

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    val fallbackStyle = browser.fallbackStyle()
    val contentColor = if (fallbackStyle.backgroundColor.luminance() > 0.45f) Color.Black else Color.White
    val textStyle = if (size >= 48.dp) {
        MaterialTheme.typography.titleMedium
    } else {
        MaterialTheme.typography.titleSmall
    }

    Surface(
        modifier = modifier.size(size),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
                .background(fallbackStyle.backgroundColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = fallbackStyle.label,
                style = textStyle,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = contentColor,
            )
        }
    }
}

private data class BrowserFallbackStyle(
    val label: String,
    val backgroundColor: Color,
)

private fun Browser.fallbackStyle(): BrowserFallbackStyle = when (this) {
    Browser.CHROME -> BrowserFallbackStyle(label = "CH", backgroundColor = Color(0xFF1A73E8))
    Browser.SAFARI -> BrowserFallbackStyle(label = "SA", backgroundColor = Color(0xFF0A84FF))
    Browser.EDGE -> BrowserFallbackStyle(label = "ED", backgroundColor = Color(0xFF0E9FF2))
    Browser.FIREFOX -> BrowserFallbackStyle(label = "FX", backgroundColor = Color(0xFFFF7139))
    Browser.SAMSUNG_INTERNET -> BrowserFallbackStyle(label = "SI", backgroundColor = Color(0xFF5C2D91))
    Browser.OPERA -> BrowserFallbackStyle(label = "OP", backgroundColor = Color(0xFFFF1B2D))
    Browser.BRAVE -> BrowserFallbackStyle(label = "BR", backgroundColor = Color(0xFFFF6B2B))
    Browser.YANDEX -> BrowserFallbackStyle(label = "YA", backgroundColor = Color(0xFFE53935))
    Browser.DUCKDUCKGO -> BrowserFallbackStyle(label = "DD", backgroundColor = Color(0xFFDE5833))
    Browser.VIVALDI -> BrowserFallbackStyle(label = "VI", backgroundColor = Color(0xFFEF3939))
    Browser.ARC -> BrowserFallbackStyle(label = "AR", backgroundColor = Color(0xFF1C1C1E))
    Browser.NAVER_WHALE -> BrowserFallbackStyle(label = "WH", backgroundColor = Color(0xFF03C75A))
    Browser.KIWI -> BrowserFallbackStyle(label = "KI", backgroundColor = Color(0xFF5E8BFF))
    Browser.IE -> BrowserFallbackStyle(label = "IE", backgroundColor = Color(0xFF0078D4))
    Browser.UNKNOWN -> BrowserFallbackStyle(
        label = "?",
        backgroundColor = Color(0xFF6B7280),
    )
}
