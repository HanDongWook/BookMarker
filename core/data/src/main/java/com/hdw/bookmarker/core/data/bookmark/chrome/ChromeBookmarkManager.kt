package com.hdw.bookmarker.core.data.bookmark.chrome

import android.content.Context
import android.net.Uri
import com.hdw.bookmarker.core.model.bookmark.error.BookmarkImportError
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChromeBookmarkManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val parser = ChromeBookmarkParser()

    fun parsingHtml(uri: Uri): BookmarkImportResult {
        return try {
            val htmlContent = context.contentResolver.openInputStream(uri)
                ?.bufferedReader(Charsets.UTF_8)
                ?.use { it.readText() }
                ?: return BookmarkImportResult.Failure(
                    error = BookmarkImportError.INVALID_URI,
                    message = "Cannot open uri: $uri"
                )

            if (htmlContent.isBlank()) {
                return BookmarkImportResult.Failure(
                    error = BookmarkImportError.EMPTY_CONTENT,
                    message = "Bookmark html is empty."
                )
            }

            val document = parser.getBookmarkDocument(htmlContent)
            BookmarkImportResult.Success(document)
        } catch (exception: SecurityException) {
            Timber.e(exception, "Permission denied while reading bookmark html. uri=%s", uri)
            BookmarkImportResult.Failure(
                error = BookmarkImportError.PERMISSION_DENIED,
                message = exception.message
            )
        } catch (exception: FileNotFoundException) {
            Timber.e(exception, "Bookmark html file not found. uri=%s", uri)
            BookmarkImportResult.Failure(
                error = BookmarkImportError.FILE_NOT_FOUND,
                message = exception.message
            )
        } catch (exception: IOException) {
            Timber.e(exception, "I/O error while reading bookmark html. uri=%s", uri)
            BookmarkImportResult.Failure(
                error = BookmarkImportError.IO_ERROR,
                message = exception.message
            )
        } catch (exception: IllegalArgumentException) {
            Timber.e(exception, "Invalid bookmark uri. uri=%s", uri)
            BookmarkImportResult.Failure(
                error = BookmarkImportError.INVALID_URI,
                message = exception.message
            )
        } catch (exception: Exception) {
            Timber.e(exception, "Unknown parse error while reading bookmark html. uri=%s", uri)
            BookmarkImportResult.Failure(
                error = BookmarkImportError.PARSE_ERROR,
                message = exception.message
            )
        }
    }
}
