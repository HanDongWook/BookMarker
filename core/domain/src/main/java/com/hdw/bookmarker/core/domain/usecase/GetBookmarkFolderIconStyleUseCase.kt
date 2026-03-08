package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetBookmarkFolderIconStyleUseCase @Inject constructor(private val settingsRepository: SettingsRepository) {
    operator fun invoke(): Flow<BookmarkFolderIconStyle> = combine(
        settingsRepository.getBookmarkFolderIconShapeFlow(),
        settingsRepository.getBookmarkFolderIconColorFlow(),
    ) { shape, color ->
        BookmarkFolderIconStyle(
            shape = BookmarkFolderIconShape.fromPersisted(shape),
            color = BookmarkFolderIconColor.fromPersisted(color),
        )
    }
}
