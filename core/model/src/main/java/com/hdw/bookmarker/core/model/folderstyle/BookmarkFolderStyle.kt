package com.hdw.bookmarker.core.model.folderstyle

data class BookmarkFolderIconStyle(
    val shape: BookmarkFolderIconShape = BookmarkFolderIconShape.FILLED,
    val color: BookmarkFolderIconColor = BookmarkFolderIconColor.DEFAULT,
)

enum class BookmarkFolderIconShape {
    FILLED,
    OUTLINED,
    ;

    companion object {
        fun fromPersisted(value: String?): BookmarkFolderIconShape = entries.firstOrNull { it.name == value } ?: FILLED
    }
}

enum class BookmarkFolderIconColor {
    DEFAULT,
    BLUE,
    GREEN,
    ORANGE,
    PURPLE,
    ;

    companion object {
        fun fromPersisted(value: String?): BookmarkFolderIconColor = entries.firstOrNull { it.name == value } ?: DEFAULT
    }
}
