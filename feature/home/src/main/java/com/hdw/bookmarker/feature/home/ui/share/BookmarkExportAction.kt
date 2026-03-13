package com.hdw.bookmarker.feature.home.ui.share

enum class BookmarkExportMethod {
    SHARE,
    SAVE,
}

enum class BookmarkExportFormat {
    TEXT,
    HTML,
}

data class BookmarkExportAction(
    val method: BookmarkExportMethod,
    val format: BookmarkExportFormat,
)
