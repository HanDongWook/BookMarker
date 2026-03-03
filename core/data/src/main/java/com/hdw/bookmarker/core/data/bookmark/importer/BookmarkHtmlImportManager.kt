package com.hdw.bookmarker.core.data.bookmark.importer

import android.net.Uri
import com.hdw.bookmarker.core.common.BookmarkerDispatchers
import com.hdw.bookmarker.core.common.Dispatcher
import com.hdw.bookmarker.core.data.bookmark.parser.BookmarkHtmlParser
import com.hdw.bookmarker.core.data.file.ContentFileManager
import com.hdw.bookmarker.core.model.bookmark.error.BookmarkImportError
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import com.hdw.bookmarker.core.model.file.error.ContentFileError
import com.hdw.bookmarker.core.model.file.result.ContentFileResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkHtmlImportManager @Inject constructor(
    private val contentFileManager: ContentFileManager,
    @param:Dispatcher(BookmarkerDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
) {
    private val parser = BookmarkHtmlParser()

    suspend fun parseHtml(uri: Uri): BookmarkImportResult {
        val htmlContent = when (val fileResult = contentFileManager.readUtf8Text(uri)) {
            is ContentFileResult.Success -> fileResult.data

            is ContentFileResult.Failure -> {
                return BookmarkImportResult.Failure(
                    error = fileResult.error.toBookmarkImportError(),
                    message = fileResult.message,
                )
            }
        }

        return try {
            val document = withContext(defaultDispatcher) {
                parser.getBookmarkDocument(htmlContent)
            }
            BookmarkImportResult.Success(document)
        } catch (exception: Exception) {
            Timber.e(exception, "Unknown parse error while reading bookmark html. uri=%s", uri)
            BookmarkImportResult.Failure(
                error = BookmarkImportError.PARSE_ERROR,
                message = exception.message,
            )
        }
    }

    private fun ContentFileError.toBookmarkImportError(): BookmarkImportError = when (this) {
        ContentFileError.INVALID_URI -> BookmarkImportError.INVALID_URI
        ContentFileError.FILE_NOT_FOUND -> BookmarkImportError.FILE_NOT_FOUND
        ContentFileError.PERMISSION_DENIED -> BookmarkImportError.PERMISSION_DENIED
        ContentFileError.IO_ERROR -> BookmarkImportError.IO_ERROR
        ContentFileError.EMPTY_CONTENT -> BookmarkImportError.EMPTY_CONTENT
        ContentFileError.UNKNOWN -> BookmarkImportError.UNKNOWN
    }
}
