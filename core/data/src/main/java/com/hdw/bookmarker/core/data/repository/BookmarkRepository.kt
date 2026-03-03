package com.hdw.bookmarker.core.data.repository

import android.net.Uri
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.model.file.result.ContentFileResult
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    suspend fun getBookmarks(browser: Browser, uri: Uri): BookmarkImportResult

    suspend fun getRawFileHash(uri: Uri): ContentFileResult<String>

    fun getBookmarkSnapshotsFlow(): Flow<Map<String, BookmarkDocument>>

    fun getOrderedSnapshotIdsFlow(): Flow<List<String>>

    /**
     * @param snapshotId null이면 새 스냅샷 생성, 아니면 해당 ID 덮어쓰기
     * @return 저장에 사용된 snapshotId
     */
    suspend fun saveBookmarkSnapshot(
        snapshotId: String?,
        document: BookmarkDocument,
        sourceHash: String = "",
        bookmarkColor: Long,
    ): String

    suspend fun clearBookmarkSnapshot(snapshotId: String)

    suspend fun getBookmarkSnapshotRawFileHash(snapshotId: String): String?

    suspend fun getBookmarkColor(snapshotId: String): Long?

    suspend fun setBookmarkColor(snapshotId: String, bookmarkColor: Long)
}
