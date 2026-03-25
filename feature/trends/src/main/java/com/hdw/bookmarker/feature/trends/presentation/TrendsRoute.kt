package com.hdw.bookmarker.feature.trends.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerTheme
import com.hdw.bookmarker.feature.trends.presentation.component.TrendsScreen

@Composable
fun TrendsRoute() {
    TrendsScreen()
}

@Preview(showBackground = true)
@Composable
private fun TrendsRoutePreview() {
    BookMarkerTheme {
        TrendsRoute()
    }
}
