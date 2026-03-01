package com.hdw.bookmarker.feature.importguide

import androidx.compose.runtime.Composable
import com.hdw.bookmarker.core.model.browser.Browser
import kotlinx.serialization.Serializable

@Serializable
sealed interface ImportGuideRoute {
    @Serializable
    data object Picker : ImportGuideRoute

    @Serializable
    data class Guide(val packageName: String) : ImportGuideRoute
}

@Composable
fun BookmarkImportGuideEntry(
    onBackClick: () -> Unit,
    onOpenDesktopGuide: (Browser, String?) -> Boolean,
) {
    BrowserPickerRoute(
        onBackClick = onBackClick,
        onOpenDesktopGuide = onOpenDesktopGuide,
    )
}
