package com.hdw.bookmarker.feature.importguide

import androidx.compose.runtime.Composable
import com.hdw.bookmarker.core.model.browser.Browser

@Composable
fun BookmarkImportGuideRoute(onBackClick: () -> Unit, onOpenDesktopGuide: (Browser, String?) -> Boolean) {
    BrowserPickerRoute(
        onBackClick = onBackClick,
        onOpenDesktopGuide = onOpenDesktopGuide,
    )
}
