package com.hdw.bookmarker.feature.home.snapshot

import android.content.Context
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.ui.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SnapshotTitleGenerator @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    fun nextDefaultTitle(existingDocuments: Collection<BookmarkDocument>): String {
        val titlePrefix = context.getString(R.string.default_snapshot_title_prefix)
        val maxNumber = existingDocuments
            .mapNotNull { document ->
                document.title
                    ?.trim()
                    ?.takeIf { it.startsWith(titlePrefix) }
                    ?.removePrefix(titlePrefix)
                    ?.toIntOrNull()
            }
            .maxOrNull()
            ?: 0
        return "$titlePrefix${maxNumber + 1}"
    }
}
