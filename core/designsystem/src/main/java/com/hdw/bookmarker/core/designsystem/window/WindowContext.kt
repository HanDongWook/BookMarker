package com.hdw.bookmarker.core.designsystem.window

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration

const val LARGE_WINDOW_MIN_WIDTH_DP = 840

@Immutable
data class WindowContext(
    val screenWidthDp: Int,
    val screenHeightDp: Int,
    val isLandscape: Boolean,
    val isLargeWidth: Boolean,
)

val LocalWindowContext = staticCompositionLocalOf {
    WindowContext(
        screenWidthDp = 0,
        screenHeightDp = 0,
        isLandscape = false,
        isLargeWidth = false,
    )
}

@Composable
fun ProvideWindowContext(
    content: @Composable () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val windowContext = WindowContext(
        screenWidthDp = configuration.screenWidthDp,
        screenHeightDp = configuration.screenHeightDp,
        isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
        isLargeWidth = configuration.screenWidthDp >= LARGE_WINDOW_MIN_WIDTH_DP,
    )

    CompositionLocalProvider(
        LocalWindowContext provides windowContext,
        content = content,
    )
}
