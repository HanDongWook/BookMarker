package com.hdw.bookmarker.feature.home.ui.preview.webview

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun BookmarkPreviewWebView(
    url: String,
    refreshToken: Int,
    onPageStarted: () -> Unit,
    onPageFinished: () -> Unit,
    onPageTitleChange: (String?) -> Unit,
    onOpenExternally: (String) -> Unit,
    onPageError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val requestState = remember { BookmarkPreviewWebViewRequestState(refreshToken) }
    val currentOnPageStarted = rememberUpdatedState(onPageStarted)
    val currentOnPageFinished = rememberUpdatedState(onPageFinished)
    val currentOnPageTitleChange = rememberUpdatedState(onPageTitleChange)
    val currentOnOpenExternally = rememberUpdatedState(onOpenExternally)
    val currentOnPageError = rememberUpdatedState(onPageError)
    requestState.syncRefreshToken(refreshToken)
    AndroidView(
        modifier = modifier,
        factory = {
            WebView(context).apply {
                configureBookmarkPreviewWebView()
                webChromeClient = BookmarkPreviewWebChromeClient(
                    onPageTitleChange = { title -> currentOnPageTitleChange.value(title) },
                    onPageFinished = { currentOnPageFinished.value() },
                )
                webViewClient = BookmarkPreviewWebViewClient(
                    requestState = requestState,
                    onPageStarted = { currentOnPageStarted.value() },
                    onPageFinished = { currentOnPageFinished.value() },
                    onOpenExternally = { urlToOpen -> currentOnOpenExternally.value(urlToOpen) },
                    onPageError = { currentOnPageError.value() },
                )
                requestState.markRequested(url)
                loadUrl(url)
            }
        },
        update = { webView ->
            when {
                requestState.shouldLoad(url) -> {
                    requestState.markRequested(url)
                    webView.loadUrl(url)
                }
                requestState.shouldRefresh() -> {
                    requestState.markRequested(url)
                    webView.reload()
                }
            }
        },
        onRelease = { webView ->
            webView.stopLoading()
            webView.destroy()
        },
    )
}
