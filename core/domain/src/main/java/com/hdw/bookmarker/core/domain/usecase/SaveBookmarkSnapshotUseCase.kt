package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import com.hdw.bookmarker.core.domain.util.BookmarkColorGenerator
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import javax.inject.Inject

class SaveBookmarkSnapshotUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
) {
    suspend operator fun invoke(
        browserPackage: String,
        document: BookmarkDocument,
        sourceHash: String = "",
        bookmarkColor: Long? = null,
    ) {
        val colorToSave = bookmarkColor
            ?: bookmarkRepository.getBookmarkColor(browserPackage)
            ?: BookmarkColorGenerator.generateColorForPackage(browserPackage)

        bookmarkRepository.saveBookmarkSnapshot(
            browserPackage = browserPackage,
            document = document,
            sourceHash = sourceHash,
            bookmarkColor = colorToSave,
        )
    }
}
