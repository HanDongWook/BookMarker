package com.hdw.bookmarker.core.data.bookmark

import com.hdw.bookmarker.core.model.bookmark.Bookmark
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import java.io.InputStream

interface BookmarkParser {
    fun getBookmarkDocument(html: String): BookmarkDocument
}