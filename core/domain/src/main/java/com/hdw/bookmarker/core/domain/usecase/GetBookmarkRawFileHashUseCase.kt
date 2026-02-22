package com.hdw.bookmarker.core.domain.usecase

import android.net.Uri
import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import com.hdw.bookmarker.core.model.file.result.ContentFileResult
import javax.inject.Inject

class GetBookmarkRawFileHashUseCase @Inject constructor(private val bookmarkRepository: BookmarkRepository) {
    operator fun invoke(uri: Uri): ContentFileResult<String> = bookmarkRepository.getRawFileHash(uri)
}
