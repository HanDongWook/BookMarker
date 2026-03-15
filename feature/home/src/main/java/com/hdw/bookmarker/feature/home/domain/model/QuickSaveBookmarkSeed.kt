package com.hdw.bookmarker.feature.home.domain.model

data class QuickSaveBookmarkSeed(
    val title: String,
    val url: String,
    val description: String = "",
    val tags: List<String> = emptyList(),
)
