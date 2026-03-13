package com.hdw.bookmarker.feature.home.ui.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.R

@Composable
fun BookmarkPreviewPane(
    url: String?,
    refreshToken: Int,
    onOpenExternally: (String) -> Unit,
    onClose: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pageTitle by remember(url) { mutableStateOf<String?>(null) }
    var isLoading by remember(url, refreshToken) { mutableStateOf(url != null) }
    var hasError by remember(url, refreshToken) { mutableStateOf(false) }

    BookmarkPreviewPaneContent(
        title = pageTitle,
        currentUrl = url,
        isLoading = isLoading,
        hasError = hasError,
        onOpenExternally = onOpenExternally,
        onClose = onClose,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        BookmarkPreviewWebView(
            url = url.orEmpty(),
            refreshToken = refreshToken,
            onPageStarted = {
                isLoading = true
                hasError = false
            },
            onPageFinished = {
                isLoading = false
            },
            onPageTitleChange = { title ->
                pageTitle = title
            },
            onOpenExternally = onOpenExternally,
            onPageError = {
                isLoading = false
                hasError = true
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
internal fun BookmarkPreviewPaneContent(
    title: String?,
    currentUrl: String?,
    isLoading: Boolean,
    hasError: Boolean,
    onOpenExternally: (String) -> Unit,
    onClose: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    body: @Composable BoxScope.() -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize()) {
        BookmarkPreviewTopBar(
            title = title,
            currentUrl = currentUrl,
            onOpenExternally = onOpenExternally,
            onRefresh = onRefresh,
            onClose = onClose,
        )

        when {
            currentUrl.isNullOrBlank() -> {
                BookmarkPreviewPlaceholder(
                    text = stringResource(R.string.bookmark_preview_placeholder),
                    modifier = Modifier.weight(1f),
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    body()

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    } else if (hasError) {
                        BookmarkPreviewPlaceholder(
                            text = stringResource(R.string.bookmark_preview_failed),
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkPreviewPaneEmptyPreview() {
    BookmarkPreviewPaneContent(
        title = null,
        currentUrl = null,
        isLoading = false,
        hasError = false,
        onOpenExternally = {},
        onClose = {},
        onRefresh = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BookmarkPreviewPaneLoadingPreview() {
    BookmarkPreviewPaneContent(
        title = "OpenAI",
        currentUrl = "https://openai.com",
        isLoading = true,
        hasError = false,
        onOpenExternally = {},
        onClose = {},
        onRefresh = {},
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkPreviewPaneErrorPreview() {
    BookmarkPreviewPaneContent(
        title = "Broken page",
        currentUrl = "https://example.com",
        isLoading = false,
        hasError = true,
        onOpenExternally = {},
        onClose = {},
        onRefresh = {},
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}
