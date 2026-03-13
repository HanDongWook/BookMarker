package com.hdw.bookmarker.feature.home.contract

data class QuickSaveBookmarkSeed(
    val title: String,
    val url: String,
    val note: String = "",
    val tags: List<String> = emptyList(),
)
