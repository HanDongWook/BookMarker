package com.hdw.bookmarker.core.model

data class Bookmark(
    val id: Long,
    val title: String,
    val url: String,
    val folder: String? = null,
    val createdAt: Long? = null,
)
