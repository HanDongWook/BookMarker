package com.hdw.bookmarker.feature.home.ui.preview.webview

internal class BookmarkPreviewWebViewRequestState(
    initialRefreshToken: Int,
) {
    private var latestRefreshToken: Int = initialRefreshToken
    private var handledRefreshToken: Int = initialRefreshToken
    private var lastRequestedUrl: String? = null
    private var lastCleartextUpgradeSourceUrl: String? = null

    fun syncRefreshToken(refreshToken: Int) {
        latestRefreshToken = refreshToken
    }

    fun markRequested(url: String) {
        lastRequestedUrl = url
        handledRefreshToken = latestRefreshToken
        lastCleartextUpgradeSourceUrl = null
    }

    fun shouldLoad(url: String): Boolean = lastRequestedUrl != url

    fun shouldRefresh(): Boolean = latestRefreshToken != handledRefreshToken

    fun canRetryCleartextUpgrade(url: String): Boolean = lastCleartextUpgradeSourceUrl != url

    fun markCleartextUpgrade(url: String) {
        lastCleartextUpgradeSourceUrl = url
    }
}
