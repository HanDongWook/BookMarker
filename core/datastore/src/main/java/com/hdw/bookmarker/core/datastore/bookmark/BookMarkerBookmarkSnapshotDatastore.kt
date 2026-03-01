package com.hdw.bookmarker.core.datastore.bookmark

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.hdw.bookmarker.core.datastore.proto.BookmarkSnapshotsProto
import com.hdw.bookmarker.core.datastore.proto.BrowserBookmarkSnapshotProto
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val BOOKMARK_SNAPSHOT_DATASTORE_NAME = "bookmark_snapshot.pb"
private const val BOOKMARK_SNAPSHOT_SCHEMA_VERSION = 1

private val Context.bookmarkSnapshotDataStore: DataStore<BookmarkSnapshotsProto> by dataStore(
    fileName = BOOKMARK_SNAPSHOT_DATASTORE_NAME,
    serializer = BookmarkSnapshotsSerializer,
)

@Singleton
class BookMarkerBookmarkSnapshotDatastore @Inject constructor(@param:ApplicationContext private val context: Context) {

    private val dataStore: DataStore<BookmarkSnapshotsProto>
        get() = context.bookmarkSnapshotDataStore

    fun getSnapshotsFlow(): Flow<Map<String, BookmarkDocument>> = dataStore.data
        .catch { exception ->
            if (exception is java.io.IOException) {
                emit(BookmarkSnapshotsProto())
            } else {
                throw exception
            }
        }
        .map { snapshots ->
            snapshots.snapshots.associate { snapshot ->
                snapshot.snapshotId to snapshot.document.toModel()
            }
        }

    fun getOrderedSnapshotIdsFlow(): Flow<List<String>> = dataStore.data
        .catch { exception ->
            if (exception is java.io.IOException) {
                emit(BookmarkSnapshotsProto())
            } else {
                throw exception
            }
        }
        .map { snapshots ->
            snapshots.snapshots.map { it.snapshotId }
        }

    fun getBookmarkColorsFlow(): Flow<Map<String, Long>> = dataStore.data
        .catch { exception ->
            if (exception is java.io.IOException) {
                emit(BookmarkSnapshotsProto())
            } else {
                throw exception
            }
        }
        .map { snapshots ->
            snapshots.snapshots.associate { snapshot ->
                snapshot.snapshotId to snapshot.bookmarkColor
            }
        }

    /**
     * @param snapshotId null이면 새 UUID로 생성, 아니면 해당 스냅샷 덮어쓰기
     * @return 저장에 사용된 snapshotId
     */
    suspend fun saveSnapshot(
        snapshotId: String?,
        document: BookmarkDocument,
        importedAtEpochMs: Long = System.currentTimeMillis(),
        sourceHash: String = "",
        bookmarkColor: Long,
    ): String {
        val id = snapshotId ?: UUID.randomUUID().toString()
        dataStore.updateData { current ->
            val nextSnapshot = BrowserBookmarkSnapshotProto(
                snapshotId = id,
                importedAtEpochMs = importedAtEpochMs,
                sourceHash = sourceHash,
                document = document.toProto(),
                bookmarkColor = bookmarkColor,
            )
            val updatedSnapshots = current.snapshots
                .filterNot { it.snapshotId == id } + nextSnapshot

            current.copy(
                schemaVersion = BOOKMARK_SNAPSHOT_SCHEMA_VERSION,
                snapshots = updatedSnapshots,
            )
        }
        return id
    }

    suspend fun updateBookmarkColor(snapshotId: String, bookmarkColor: Long) {
        dataStore.updateData { current ->
            val updatedSnapshots = current.snapshots.map { snapshot ->
                if (snapshot.snapshotId == snapshotId) {
                    snapshot.copy(bookmarkColor = bookmarkColor)
                } else {
                    snapshot
                }
            }
            current.copy(
                schemaVersion = BOOKMARK_SNAPSHOT_SCHEMA_VERSION,
                snapshots = updatedSnapshots,
            )
        }
    }

    suspend fun clearSnapshot(snapshotId: String) {
        dataStore.updateData { current ->
            val updatedSnapshots = current.snapshots.filterNot { it.snapshotId == snapshotId }
            current.copy(
                schemaVersion = BOOKMARK_SNAPSHOT_SCHEMA_VERSION,
                snapshots = updatedSnapshots,
            )
        }
    }

    suspend fun getRawFileHash(snapshotId: String): String? = dataStore.data.first().snapshots
        .firstOrNull { it.snapshotId == snapshotId }
        ?.sourceHash
        ?.takeIf { it.isNotBlank() }
}
