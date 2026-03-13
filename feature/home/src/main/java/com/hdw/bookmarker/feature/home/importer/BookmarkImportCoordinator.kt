package com.hdw.bookmarker.feature.home.importer

import android.net.Uri
import androidx.annotation.StringRes
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkRawFileHashUseCase
import com.hdw.bookmarker.core.domain.usecase.ImportBookmarksFromHtmlUseCase
import com.hdw.bookmarker.core.domain.usecase.SaveBookmarkSnapshotUseCase
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.error.BookmarkImportError
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import com.hdw.bookmarker.core.model.file.error.ContentFileError
import com.hdw.bookmarker.core.model.file.result.ContentFileResult
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.home.snapshot.SnapshotTitleGenerator
import timber.log.Timber
import javax.inject.Inject

class BookmarkImportCoordinator @Inject constructor(
    private val getBookmarkRawFileHashUseCase: GetBookmarkRawFileHashUseCase,
    private val importBookmarksFromHtmlUseCase: ImportBookmarksFromHtmlUseCase,
    private val saveBookmarkSnapshotUseCase: SaveBookmarkSnapshotUseCase,
    private val snapshotTitleGenerator: SnapshotTitleGenerator,
) {
    suspend fun importHtml(
        uri: Uri,
        existingDocuments: Collection<BookmarkDocument>,
    ): BookmarkImportCoordinatorResult {
        val rawFileHash = when (val hashResult = getBookmarkRawFileHashUseCase(uri)) {
            is ContentFileResult.Success -> hashResult.data

            is ContentFileResult.Failure -> {
                return BookmarkImportCoordinatorResult.Failure(
                    messageResId = hashResult.error.toUiMessageResId(),
                    detail = hashResult.message,
                )
            }
        }

        return when (val result = importBookmarksFromHtmlUseCase(uri = uri)) {
            is BookmarkImportResult.Success -> {
                val snapshotTitle = snapshotTitleGenerator.nextDefaultTitle(existingDocuments)
                val savedSnapshotId = saveBookmarkSnapshotUseCase(
                    snapshotId = null,
                    document = result.document.copy(title = snapshotTitle),
                    sourceHash = rawFileHash,
                )
                BookmarkImportCoordinatorResult.Success(snapshotId = savedSnapshotId)
            }

            is BookmarkImportResult.Failure -> {
                Timber.e(
                    "Bookmark html import failed. error=%s, message=%s",
                    result.error,
                    result.message,
                )
                BookmarkImportCoordinatorResult.Failure(
                    messageResId = result.error.toUiMessageResId(),
                    detail = result.message,
                )
            }
        }
    }

    @StringRes
    private fun BookmarkImportError.toUiMessageResId(): Int = when (this) {
        BookmarkImportError.INVALID_URI -> R.string.error_invalid_uri
        BookmarkImportError.FILE_NOT_FOUND -> R.string.error_file_not_found
        BookmarkImportError.PERMISSION_DENIED -> R.string.error_permission_denied
        BookmarkImportError.IO_ERROR -> R.string.error_io
        BookmarkImportError.EMPTY_CONTENT -> R.string.error_empty_content
        BookmarkImportError.PARSE_ERROR -> R.string.error_parse
        BookmarkImportError.UNSUPPORTED_BROWSER -> R.string.error_unsupported_browser
        BookmarkImportError.UNKNOWN -> R.string.error_unknown
    }

    @StringRes
    private fun ContentFileError.toUiMessageResId(): Int = when (this) {
        ContentFileError.INVALID_URI -> R.string.error_invalid_uri
        ContentFileError.FILE_NOT_FOUND -> R.string.error_file_not_found
        ContentFileError.PERMISSION_DENIED -> R.string.error_permission_denied
        ContentFileError.IO_ERROR -> R.string.error_io
        ContentFileError.EMPTY_CONTENT -> R.string.error_empty_content
        ContentFileError.UNKNOWN -> R.string.error_unknown
    }
}
