package com.hdw.bookmarker.core.model.file.result

import com.hdw.bookmarker.core.model.file.error.ContentFileError

sealed interface ContentFileResult<out T> {
    data class Success<T>(val data: T) : ContentFileResult<T>

    data class Failure(val error: ContentFileError, val message: String? = null) : ContentFileResult<Nothing>
}
