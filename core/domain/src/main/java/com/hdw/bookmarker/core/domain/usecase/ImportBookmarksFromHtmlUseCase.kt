package com.hdw.bookmarker.core.domain.usecase

import android.net.Uri
import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import javax.inject.Inject

class ImportBookmarksFromHtmlUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
) {
    suspend operator fun invoke(uri: Uri): BookmarkImportResult =
        bookmarkRepository.importBookmarksFromHtml(uri)
}
