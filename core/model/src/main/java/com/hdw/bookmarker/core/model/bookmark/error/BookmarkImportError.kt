package com.hdw.bookmarker.core.model.bookmark.error

enum class BookmarkImportError {
    INVALID_URI,
    FILE_NOT_FOUND,
    PERMISSION_DENIED,
    IO_ERROR,
    EMPTY_CONTENT,
    PARSE_ERROR,
    UNSUPPORTED_BROWSER,
    UNKNOWN,
}
