package com.hdw.bookmarker.feature.home.ui.preview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
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

    Column(modifier = modifier.fillMaxSize()) {
        PreviewPaneTopBar(
            title = pageTitle,
            currentUrl = url,
            onOpenExternally = onOpenExternally,
            onRefresh = onRefresh,
            onClose = onClose,
        )

        when {
            url.isNullOrBlank() -> {
                PreviewPlaceholder(
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
                    PreviewWebView(
                        url = url,
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
                    )

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    } else if (hasError) {
                        PreviewPlaceholder(
                            text = stringResource(R.string.bookmark_preview_failed),
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewPaneTopBar(
    title: String?,
    currentUrl: String?,
    onOpenExternally: (String) -> Unit,
    onRefresh: () -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title?.takeIf { it.isNotBlank() } ?: stringResource(R.string.bookmark_preview_title),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = onRefresh,
            enabled = !currentUrl.isNullOrBlank(),
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.bookmark_preview_refresh),
            )
        }
        IconButton(
            onClick = { currentUrl?.let(onOpenExternally) },
            enabled = !currentUrl.isNullOrBlank(),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = stringResource(R.string.bookmark_preview_open_externally),
            )
        }
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.bookmark_preview_close),
            )
        }
    }
}

@Composable
private fun PreviewPlaceholder(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PreviewWebView(
    url: String,
    refreshToken: Int,
    onPageStarted: () -> Unit,
    onPageFinished: () -> Unit,
    onPageTitleChange: (String?) -> Unit,
    onOpenExternally: (String) -> Unit,
    onPageError: () -> Unit,
) {
    val context = LocalContext.current
    androidx.compose.runtime.key(url, refreshToken) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    settings.setSupportMultipleWindows(false)
                    webChromeClient = object : WebChromeClient() {
                        override fun onReceivedTitle(view: WebView?, title: String?) {
                            onPageTitleChange(title)
                        }
                    }
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?,
                        ): Boolean {
                            val targetUrl = request?.url?.toString().orEmpty()
                            val scheme = request?.url?.scheme.orEmpty()
                            return if (scheme == "http" || scheme == "https") {
                                false
                            } else {
                                if (targetUrl.isNotBlank()) onOpenExternally(targetUrl)
                                true
                            }
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            onPageStarted()
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            onPageFinished()
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?,
                        ) {
                            if (request?.isForMainFrame == true) {
                                onPageError()
                            }
                        }
                    }
                    loadUrl(url)
                }
            },
            update = { webView ->
                val currentUri = webView.url?.toUri()
                val targetUri = url.toUri()
                if (currentUri != targetUri) {
                    webView.loadUrl(url)
                } else if (refreshToken > 0) {
                    webView.reload()
                }
            },
            onRelease = { webView ->
                webView.stopLoading()
                webView.destroy()
            },
        )
    }
}
