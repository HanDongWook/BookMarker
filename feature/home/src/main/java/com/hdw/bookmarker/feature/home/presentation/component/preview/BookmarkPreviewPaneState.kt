package com.hdw.bookmarker.feature.home.presentation.component.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Stable
class BookmarkPreviewPaneState internal constructor(initialUrl: String?, initialRefreshToken: Int) {
    var currentUrl by mutableStateOf(initialUrl)
        private set

    var refreshToken by mutableIntStateOf(initialRefreshToken)
        private set

    fun open(url: String) {
        currentUrl = url
    }

    fun clear() {
        currentUrl = null
        refreshToken = 0
    }

    fun refresh() {
        if (!currentUrl.isNullOrBlank()) {
            refreshToken += 1
        }
    }

    companion object {
        val Saver = listSaver<BookmarkPreviewPaneState, Any?>(
            save = { state ->
                listOf(state.currentUrl, state.refreshToken)
            },
            restore = { restored ->
                BookmarkPreviewPaneState(
                    initialUrl = restored[0] as String?,
                    initialRefreshToken = restored[1] as Int,
                )
            },
        )
    }
}

@Composable
fun rememberBookmarkPreviewPaneState(): BookmarkPreviewPaneState = rememberSaveable(
    saver = BookmarkPreviewPaneState.Saver,
) {
    BookmarkPreviewPaneState(
        initialUrl = null,
        initialRefreshToken = 0,
    )
}
