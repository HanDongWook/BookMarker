package com.hdw.bookmarker.core.model.bookmark

sealed class BookmarkItem {
    data class Bookmark(
        val title: String,
        val url: String,
        val addDate: String?,
        val lastModified: String?,
        val iconUri: String?,
        val description: String? = null,
        val tags: List<String> = emptyList(),
    ) : BookmarkItem()

    data class Folder(
        val title: String,
        val description: String?,
        val addDate: String?,
        val lastModified: String?,
        val children: List<BookmarkItem>,
    ) : BookmarkItem()

    fun isBookmark(): Boolean = this is Bookmark
    fun isFolder(): Boolean = this is Folder
}
