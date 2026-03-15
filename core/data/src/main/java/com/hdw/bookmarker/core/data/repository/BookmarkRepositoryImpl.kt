package com.hdw.bookmarker.core.data.repository

import android.net.Uri
import com.hdw.bookmarker.core.data.bookmark.importer.BookmarkHtmlImportManager
import com.hdw.bookmarker.core.data.file.ContentFileManager
import com.hdw.bookmarker.core.datastore.bookmark.BookMarkerBookmarkSnapshotDatastore
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import com.hdw.bookmarker.core.model.file.result.ContentFileResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkHtmlImportManager: BookmarkHtmlImportManager,
    private val contentFileManager: ContentFileManager,
    private val bookmarkSnapshotDatastore: BookMarkerBookmarkSnapshotDatastore,
) : BookmarkRepository {
    override suspend fun importBookmarksFromHtml(uri: Uri): BookmarkImportResult =
        bookmarkHtmlImportManager.parseHtml(uri)

    override suspend fun getRawFileHash(uri: Uri): ContentFileResult<String> = contentFileManager.getRawFileHash(uri)

    override fun getBookmarkSnapshotsFlow(): Flow<Map<String, BookmarkDocument>> =
        bookmarkSnapshotDatastore.getSnapshotsFlow()

    override fun getOrderedSnapshotIdsFlow(): Flow<List<String>> = bookmarkSnapshotDatastore.getOrderedSnapshotIdsFlow()

    override suspend fun saveBookmarkSnapshot(
        snapshotId: String?,
        document: BookmarkDocument,
        sourceHash: String,
        bookmarkColor: Long,
    ): String = bookmarkSnapshotDatastore.saveSnapshot(
        snapshotId = snapshotId,
        document = document,
        sourceHash = sourceHash,
        bookmarkColor = bookmarkColor,
    )

    override suspend fun clearBookmarkSnapshot(snapshotId: String) {
        bookmarkSnapshotDatastore.clearSnapshot(snapshotId)
    }

    override suspend fun getBookmarkSnapshotRawFileHash(snapshotId: String): String? =
        bookmarkSnapshotDatastore.getRawFileHash(snapshotId)

    override suspend fun getBookmarkColor(snapshotId: String): Long? =
        bookmarkSnapshotDatastore.getBookmarkColorsFlow().first()[snapshotId]

    override suspend fun setBookmarkColor(snapshotId: String, bookmarkColor: Long) {
        bookmarkSnapshotDatastore.updateBookmarkColor(
            snapshotId = snapshotId,
            bookmarkColor = bookmarkColor,
        )
    }
}
