package com.hdw.bookmarker.feature.home.ui.preview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri

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
    androidx.compose.runtime.key(url, refreshToken) {
        AndroidView(
            modifier = modifier,
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
