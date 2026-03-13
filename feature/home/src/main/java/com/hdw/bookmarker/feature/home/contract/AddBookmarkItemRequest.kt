package com.hdw.bookmarker.feature.home.contract

sealed interface AddBookmarkItemRequest {
    val parentFolderPath: List<Int>?
    val title: String

    data class Bookmark(override val parentFolderPath: List<Int>?, override val title: String, val url: String) :
        AddBookmarkItemRequest

    data class Folder(override val parentFolderPath: List<Int>?, override val title: String, val description: String) :
        AddBookmarkItemRequest
}
