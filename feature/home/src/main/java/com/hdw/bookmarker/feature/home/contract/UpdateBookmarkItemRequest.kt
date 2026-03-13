package com.hdw.bookmarker.feature.home.contract

sealed interface UpdateBookmarkItemRequest {
    val path: List<Int>
    val title: String

    data class Bookmark(
        override val path: List<Int>,
        override val title: String,
        val url: String,
        val note: String = "",
        val tags: List<String> = emptyList(),
    ) : UpdateBookmarkItemRequest

    data class Folder(override val path: List<Int>, override val title: String, val description: String) :
        UpdateBookmarkItemRequest
}
