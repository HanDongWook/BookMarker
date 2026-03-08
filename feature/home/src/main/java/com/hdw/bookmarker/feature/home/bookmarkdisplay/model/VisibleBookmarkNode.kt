package com.hdw.bookmarker.feature.home.bookmarkdisplay.model

import com.hdw.bookmarker.core.model.bookmark.BookmarkItem

data class VisibleBookmarkNode(val key: String, val depth: Int, val item: BookmarkItem, val path: List<Int>)
