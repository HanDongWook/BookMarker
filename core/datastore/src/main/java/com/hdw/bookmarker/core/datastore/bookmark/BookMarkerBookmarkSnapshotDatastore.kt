package com.hdw.bookmarker.core.datastore.bookmark

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.hdw.bookmarker.core.datastore.proto.BookmarkSnapshotsProto
import com.hdw.bookmarker.core.datastore.proto.BrowserBookmarkSnapshotProto
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import dagger.hilt.android.qualifiers.ApplicationContext
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
                snapshot.browserPackage to snapshot.document.toModel()
            }
        }

    suspend fun saveSnapshot(
        browserPackage: String,
        document: BookmarkDocument,
        importedAtEpochMs: Long = System.currentTimeMillis(),
        sourceHash: String = "",
    ) {
        dataStore.updateData { current ->
            val nextSnapshot = BrowserBookmarkSnapshotProto(
                browserPackage = browserPackage,
                importedAtEpochMs = importedAtEpochMs,
                sourceHash = sourceHash,
                document = document.toProto(),
            )
            val updatedSnapshots = current.snapshots
                .filterNot { it.browserPackage == browserPackage } + nextSnapshot

            current.copy(
                schemaVersion = BOOKMARK_SNAPSHOT_SCHEMA_VERSION,
                snapshots = updatedSnapshots,
            )
        }
    }

    suspend fun clearSnapshot(browserPackage: String) {
        dataStore.updateData { current ->
            val updatedSnapshots = current.snapshots.filterNot { it.browserPackage == browserPackage }
            current.copy(
                schemaVersion = BOOKMARK_SNAPSHOT_SCHEMA_VERSION,
                snapshots = updatedSnapshots,
            )
        }
    }

    suspend fun getRawFileHash(browserPackage: String): String? = dataStore.data.first().snapshots
        .firstOrNull { it.browserPackage == browserPackage }
        ?.sourceHash
        ?.takeIf { it.isNotBlank() }
}
