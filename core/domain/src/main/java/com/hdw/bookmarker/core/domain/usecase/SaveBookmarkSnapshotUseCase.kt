package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import com.hdw.bookmarker.core.domain.util.BookmarkColorGenerator
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import javax.inject.Inject

class SaveBookmarkSnapshotUseCase @Inject constructor(private val bookmarkRepository: BookmarkRepository) {
    /**
     * @param snapshotId null이면 새 스냅샷 생성, 아니면 해당 ID 덮어쓰기
     * @return 저장에 사용된 snapshotId
     */
    suspend operator fun invoke(
        snapshotId: String?,
        document: BookmarkDocument,
        sourceHash: String = "",
        bookmarkColor: Long? = null,
    ): String {
        val colorToSave = bookmarkColor
            ?: (snapshotId?.let { bookmarkRepository.getBookmarkColor(it) })
            ?: BookmarkColorGenerator.generateColorForId(java.util.UUID.randomUUID().toString())

        return bookmarkRepository.saveBookmarkSnapshot(
            snapshotId = snapshotId,
            document = document,
            sourceHash = sourceHash,
            bookmarkColor = colorToSave,
        )
    }
}
