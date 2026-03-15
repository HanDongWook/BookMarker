package com.hdw.bookmarker.main

import android.net.Uri

internal object AppNavigationDeepLink {
    const val scheme = "bookmarker"
    const val host = "navigate"

    enum class Target(val path: String) {
        Home(path = "home"),
        Settings(path = "settings"),
        ImportGuide(path = "import-guide"),
    }

    fun toUri(target: Target): Uri = Uri.Builder()
        .scheme(scheme)
        .authority(host)
        .appendPath(target.path)
        .build()
}
