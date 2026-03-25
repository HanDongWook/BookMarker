package com.hdw.bookmarker.main

import android.content.Intent

internal class NavigationDeepLinkIntentParser {
    fun parse(intent: Intent?): AppNavigationDeepLink.Target? {
        if (intent?.action != Intent.ACTION_VIEW) return null
        val data = intent.data ?: return null
        if (data.scheme != AppNavigationDeepLink.SCHEME || data.host != AppNavigationDeepLink.HOST) {
            return null
        }
        val path = data.pathSegments.firstOrNull().orEmpty()
        return AppNavigationDeepLink.Target.entries.firstOrNull { target ->
            target.path == path
        }
    }
}
