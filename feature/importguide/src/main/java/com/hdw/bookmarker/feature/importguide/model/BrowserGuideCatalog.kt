package com.hdw.bookmarker.feature.importguide.model

import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.model.browser.BrowserInfo

internal object BrowserGuideCatalog {
    private data class Entry(
        val browser: Browser,
        val displayName: String,
        val packagePriority: List<String> = emptyList(),
        val guideLinkType: BrowserGuideLinkType = BrowserGuideLinkType.OFFICIAL,
        val requiresDesktopExport: Boolean = true,
    )

    // Ordered by broad worldwide browser usage, then by long-tail support priority.
    private val globalRankedEntries = listOf(
        Entry(
            browser = Browser.CHROME,
            displayName = "Chrome",
            packagePriority = listOf(
                "com.android.chrome",
                "com.chrome.beta",
                "com.chrome.dev",
                "com.chrome.canary",
            ),
        ),
        Entry(browser = Browser.SAFARI, displayName = "Safari"),
        Entry(
            browser = Browser.EDGE,
            displayName = "Edge",
            packagePriority = listOf(
                "com.microsoft.emmx",
                "com.microsoft.emmx.beta",
                "com.microsoft.emmx.canary",
                "com.microsoft.emmx.dev",
            ),
        ),
        Entry(
            browser = Browser.FIREFOX,
            displayName = "Firefox",
            packagePriority = listOf(
                "org.mozilla.firefox",
                "org.mozilla.firefox_beta",
                "org.mozilla.fenix",
            ),
        ),
        Entry(
            browser = Browser.SAMSUNG_INTERNET,
            displayName = "Samsung Internet",
            packagePriority = listOf("com.sec.android.app.sbrowser"),
        ),
        Entry(
            browser = Browser.OPERA,
            displayName = "Opera",
            packagePriority = listOf(
                "com.opera.browser",
                "com.opera.browser.beta",
                "com.opera.mini.native",
            ),
        ),
        Entry(
            browser = Browser.BRAVE,
            displayName = "Brave",
            packagePriority = listOf(
                "com.brave.browser",
                "com.brave.browser_beta",
                "com.brave.browser_nightly",
            ),
            guideLinkType = BrowserGuideLinkType.IN_APP,
            requiresDesktopExport = false,
        ),
        Entry(
            browser = Browser.YANDEX,
            displayName = "Yandex Browser",
            packagePriority = listOf("com.yandex.browser"),
        ),
        Entry(
            browser = Browser.DUCKDUCKGO,
            displayName = "DuckDuckGo",
            packagePriority = listOf("com.duckduckgo.mobile.android"),
        ),
        Entry(
            browser = Browser.VIVALDI,
            displayName = "Vivaldi",
            packagePriority = listOf("com.vivaldi.browser"),
        ),
        Entry(
            browser = Browser.ARC,
            displayName = "Arc Search",
            packagePriority = listOf("company.thebrowser.browser"),
        ),
        Entry(
            browser = Browser.NAVER_WHALE,
            displayName = "Whale",
            packagePriority = listOf("com.naver.whale"),
        ),
        Entry(
            browser = Browser.KIWI,
            displayName = "Kiwi Browser",
            packagePriority = listOf("com.kiwibrowser.browser"),
            guideLinkType = BrowserGuideLinkType.NONE,
        ),
        Entry(browser = Browser.IE, displayName = "Internet Explorer"),
    )

    fun buildGuideItems(installedBrowsers: List<BrowserInfo>): List<BrowserGuideItem> {
        val installedByBrowser = installedBrowsers.groupBy { installed ->
            Browser.fromPackageAndName(
                packageName = installed.packageName,
                appName = installed.appName,
            )
        }

        return globalRankedEntries.map { entry ->
            val installedBrowser = installedByBrowser[entry.browser]
                .orEmpty()
                .sortedWith(compareBy<BrowserInfo> { browserInfo ->
                    entry.packagePriority.indexOf(browserInfo.packageName).takeIf { it >= 0 } ?: Int.MAX_VALUE
                }.thenBy { it.appName })
                .firstOrNull()

            BrowserGuideItem(
                browser = entry.browser,
                displayName = entry.displayName,
                installedBrowser = installedBrowser,
                guideLinkType = entry.guideLinkType,
                requiresDesktopExport = entry.requiresDesktopExport,
            )
        }
    }

    fun findByBrowser(browser: Browser): BrowserGuideItem? = globalRankedEntries
        .firstOrNull { it.browser == browser }
        ?.let { entry ->
            BrowserGuideItem(
                browser = entry.browser,
                displayName = entry.displayName,
                guideLinkType = entry.guideLinkType,
                requiresDesktopExport = entry.requiresDesktopExport,
            )
        }

    fun defaultGuideItem(): BrowserGuideItem = globalRankedEntries.first().let { entry ->
        BrowserGuideItem(
            browser = entry.browser,
            displayName = entry.displayName,
            guideLinkType = entry.guideLinkType,
            requiresDesktopExport = entry.requiresDesktopExport,
        )
    }
}
