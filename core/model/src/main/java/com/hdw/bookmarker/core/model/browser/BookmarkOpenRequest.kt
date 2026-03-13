package com.hdw.bookmarker.core.model.browser

data class BookmarkOpenRequest(
    val url: String,
    val preferredBrowserPackage: String? = null,
    val openAdjacent: Boolean = false,
)
