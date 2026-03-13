package com.hdw.bookmarker.feature.home.ui.preview.webview

import android.graphics.Color as AndroidColor
import android.webkit.WebSettings
import android.webkit.WebView

internal fun WebView.configureBookmarkPreviewWebView() {
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.cacheMode = WebSettings.LOAD_DEFAULT
    settings.allowFileAccess = false
    settings.allowContentAccess = false
    settings.setSupportMultipleWindows(false)
    setBackgroundColor(AndroidColor.TRANSPARENT)
}
