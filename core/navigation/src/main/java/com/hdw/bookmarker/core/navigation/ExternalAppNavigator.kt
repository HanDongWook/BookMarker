package com.hdw.bookmarker.core.navigation

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.hdw.bookmarker.core.common.uri.AppUri
import com.hdw.bookmarker.core.model.browser.BookmarkOpenRequest
import com.hdw.bookmarker.core.model.browser.Browser

object ExternalAppNavigator {
    fun openBookmarkUrl(context: Context, request: BookmarkOpenRequest): Boolean = openUri(
        context = context,
        url = request.url,
        preferredBrowserPackage = request.preferredBrowserPackage,
        launchAdjacent = request.openAdjacent,
    )

    fun openBrowserBookmarkGuide(context: Context, browser: Browser, preferredBrowserPackage: String?): Boolean {
        if (browser == Browser.BRAVE && openBraveBookmarksScreen(context, preferredBrowserPackage)) {
            return true
        }
        val guideUrl = browser.toBookmarkGuideUrl() ?: return false
        return openUri(
            context = context,
            url = guideUrl,
            preferredBrowserPackage = preferredBrowserPackage,
        )
    }

    private fun openBraveBookmarksScreen(context: Context, preferredBrowserPackage: String?): Boolean {
        val packageCandidates = buildList {
            add(preferredBrowserPackage)
            add("com.brave.browser")
            add("com.brave.browser_beta")
            add("com.brave.browser_nightly")
        }.filterNotNull().distinct()

        packageCandidates.forEach { packageName ->
            if (
                openUri(
                    context = context,
                    url = AppUri.BRAVE_BOOKMARKS_PAGE_URL,
                    preferredBrowserPackage = packageName,
                    allowFallback = false,
                )
            ) {
                return true
            }
        }

        return openUri(
            context = context,
            url = AppUri.BRAVE_BOOKMARKS_PAGE_URL,
            preferredBrowserPackage = null,
        )
    }

    private fun openUri(
        context: Context,
        url: String,
        preferredBrowserPackage: String?,
        allowFallback: Boolean = true,
        launchAdjacent: Boolean = false,
    ): Boolean {
        val uri = url.toUri()
        val intents = buildList {
            if (!preferredBrowserPackage.isNullOrBlank()) {
                add(createViewIntent(uri, preferredBrowserPackage, launchAdjacent))
                if (launchAdjacent) {
                    add(createViewIntent(uri, preferredBrowserPackage, launchAdjacent = false))
                }
            }
            if (allowFallback) {
                add(createViewIntent(uri, packageName = null, launchAdjacent = launchAdjacent))
                if (launchAdjacent) {
                    add(createViewIntent(uri, packageName = null, launchAdjacent = false))
                }
            }
        }

        return intents.any { intent ->
            runCatching { context.startActivity(intent) }.isSuccess
        }
    }

    private fun createViewIntent(uri: android.net.Uri, packageName: String?, launchAdjacent: Boolean): Intent =
        Intent(Intent.ACTION_VIEW, uri).apply {
            if (!packageName.isNullOrBlank()) {
                `package` = packageName
            }
            if (launchAdjacent) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
            }
        }

    private fun Browser.toBookmarkGuideUrl(): String? = when (this) {
        Browser.CHROME -> AppUri.CHROME_BOOKMARK_EXPORT_GUIDE_URL
        Browser.BRAVE -> AppUri.BRAVE_BOOKMARK_GUIDE_URL
        Browser.EDGE -> AppUri.EDGE_BOOKMARK_GUIDE_URL
        Browser.FIREFOX -> AppUri.FIREFOX_BOOKMARK_GUIDE_URL
        Browser.SAFARI -> AppUri.SAFARI_BOOKMARK_GUIDE_URL
        Browser.NAVER_WHALE -> AppUri.NAVER_WHALE_BOOKMARK_GUIDE_URL
        Browser.SAMSUNG_INTERNET -> AppUri.SAMSUNG_INTERNET_BOOKMARK_GUIDE_URL
        Browser.OPERA -> AppUri.OPERA_BOOKMARK_GUIDE_URL
        Browser.VIVALDI -> AppUri.VIVALDI_BOOKMARK_GUIDE_URL
        Browser.DUCKDUCKGO -> AppUri.DUCKDUCKGO_BOOKMARK_GUIDE_URL
        Browser.YANDEX -> AppUri.YANDEX_BOOKMARK_GUIDE_URL
        Browser.ARC -> AppUri.ARC_BOOKMARK_GUIDE_URL
        Browser.IE -> AppUri.IE_BOOKMARK_GUIDE_URL
        Browser.KIWI, Browser.UNKNOWN -> null
    }

    fun openDownloads(context: Context): Boolean = runCatching {
        context.startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
    }.isSuccess
}
