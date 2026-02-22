package com.hdw.bookmarker.core.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BookMarkerDivider(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 20.dp,
) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    )
}
