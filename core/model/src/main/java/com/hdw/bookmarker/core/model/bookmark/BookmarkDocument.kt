package com.hdw.bookmarker.core.model.bookmark

data class BookmarkDocument(val title: String?, val metas: Map<String, String>, val rootItems: List<BookmarkItem>)
