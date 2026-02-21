package com.hdw.bookmarker.core.model.bookmark.result

import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.error.BookmarkImportError

sealed interface BookmarkImportResult {
    data class Success(val document: BookmarkDocument) : BookmarkImportResult
    data class Failure(val error: BookmarkImportError, val message: String? = null) : BookmarkImportResult
}
