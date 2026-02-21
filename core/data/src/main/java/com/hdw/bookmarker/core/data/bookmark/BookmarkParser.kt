package com.hdw.bookmarker.core.data.bookmark

import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument

interface BookmarkParser {
    fun getBookmarkDocument(html: String): BookmarkDocument
}
