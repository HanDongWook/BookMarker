package com.hdw.bookmarker.feature.home.ui.preview.webview

import android.webkit.WebChromeClient
import android.webkit.WebView

internal class BookmarkPreviewWebChromeClient(
    private val onPageTitleChange: (String?) -> Unit,
    private val onPageFinished: () -> Unit,
) : WebChromeClient() {

    override fun onReceivedTitle(view: WebView?, title: String?) {
        onPageTitleChange(title)
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        if (newProgress >= 100) {
            onPageFinished()
        }
    }
}
