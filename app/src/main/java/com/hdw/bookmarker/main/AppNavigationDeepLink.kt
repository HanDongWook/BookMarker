package com.hdw.bookmarker.main

import android.net.Uri

internal object AppNavigationDeepLink {
    const val SCHEME = "bookmarker"
    const val HOST = "navigate"

    enum class Target(val path: String) {
        Home(path = "home"),
        Settings(path = "settings"),
        ImportGuide(path = "import-guide"),
    }

    fun toUri(target: Target): Uri = Uri.Builder()
        .scheme(SCHEME)
        .authority(HOST)
        .appendPath(target.path)
        .build()
}
