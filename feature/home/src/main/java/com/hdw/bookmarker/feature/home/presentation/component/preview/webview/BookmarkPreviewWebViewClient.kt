package com.hdw.bookmarker.feature.home.presentation.component.preview.webview

import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import timber.log.Timber

internal class BookmarkPreviewWebViewClient(
    private val requestState: BookmarkPreviewWebViewRequestState,
    private val onPageStarted: () -> Unit,
    private val onPageFinished: () -> Unit,
    private val onOpenExternally: (String) -> Unit,
    private val onPageError: () -> Unit,
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val targetUrl = request?.url?.toString().orEmpty()
        val scheme = request?.url?.scheme.orEmpty()
        val isMainFrame = request?.isForMainFrame == true
        return when (scheme) {
            "https" -> false

            "http" if isMainFrame -> {
                if (!upgradeCleartextNavigation(view = view, targetUrl = targetUrl)) {
                    onPageError()
                }
                true
            }

            "http" -> false

            else -> {
                if (targetUrl.isNotBlank()) {
                    onOpenExternally(targetUrl)
                }
                true
            }
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        onPageStarted()
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        onPageFinished()
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        onPageFinished()
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?,
    ) {
        Timber.w(
            "Bookmark preview http error. mainFrame=%s status=%s url=%s",
            request?.isForMainFrame == true,
            errorResponse?.statusCode,
            request?.url,
        )
    }

    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        val isMainFrame = request?.isForMainFrame == true
        val targetUrl = request?.url?.toString().orEmpty()
        val isCleartextBlocked =
            error?.description
                ?.toString()
                ?.contains("ERR_CLEARTEXT_NOT_PERMITTED") == true
        Timber.w(
            "Bookmark preview web error. mainFrame=%s code=%s description=%s url=%s",
            isMainFrame,
            error?.errorCode,
            error?.description,
            targetUrl,
        )
        if (isMainFrame && isCleartextBlocked && upgradeCleartextNavigation(view, targetUrl)) {
            return
        }
        if (isMainFrame) {
            onPageError()
        }
    }

    private fun upgradeCleartextNavigation(view: WebView?, targetUrl: String): Boolean {
        val upgradedUrl = targetUrl
            .replaceFirst("http://", "https://")
            .takeIf { it != targetUrl }
            .orEmpty()
        Timber.w(
            "Upgrading cleartext main-frame navigation in preview. from=%s to=%s",
            targetUrl,
            upgradedUrl,
        )
        if (upgradedUrl.isBlank() || !requestState.canRetryCleartextUpgrade(targetUrl)) {
            return false
        }
        requestState.markCleartextUpgrade(targetUrl)
        onPageStarted()
        view?.loadUrl(upgradedUrl)
        return true
    }
}
