package com.hdw.bookmarker.feature.home.contract

import com.hdw.bookmarker.core.model.bookmark.BookmarkItem

sealed interface AddBookmarkItemRequest {
    val parentFolderPath: List<Int>?
    val title: String

    data class Bookmark(
        override val parentFolderPath: List<Int>?,
        override val title: String,
        val url: String,
        val description: String = "",
        val tags: List<String> = emptyList(),
        val saveToInbox: Boolean = false,
    ) : AddBookmarkItemRequest

    data class Folder(
        override val parentFolderPath: List<Int>?,
        override val title: String,
        val description: String,
        val children: List<BookmarkItem> = emptyList(),
    ) : AddBookmarkItemRequest
}
