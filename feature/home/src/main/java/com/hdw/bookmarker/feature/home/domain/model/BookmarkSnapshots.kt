package com.hdw.bookmarker.feature.home.domain.model

import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.core.model.bookmark.isInboxSnapshot

/**
 * 사용자가 보유한 모든 북마크 스냅샷들의 컬렉션.
 */
data class BookmarkSnapshots(private val snapshots: LinkedHashMap<SnapshotId, BookmarkDocument> = linkedMapOf()) {
    /** 탭 표시 순서를 보존한 snapshotId 목록 */
    val orderedIds: List<SnapshotId> get() = snapshots.keys.toList()

    val size: Int get() = snapshots.size
    val isEmpty: Boolean get() = snapshots.isEmpty()
    val values: Collection<BookmarkDocument> get() = snapshots.values

    operator fun get(snapshotId: SnapshotId): BookmarkDocument? = snapshots[snapshotId]

    fun containsKey(snapshotId: SnapshotId): Boolean = snapshots.containsKey(snapshotId)

    // ─── Inbox 도메인 로직 ───────────────────────────────────────────

    fun isInbox(snapshotId: SnapshotId): Boolean = snapshots[snapshotId]?.isInboxSnapshot() == true

    fun isEmptyInbox(snapshotId: SnapshotId): Boolean =
        isInbox(snapshotId) && snapshots[snapshotId]?.rootItems.isNullOrEmpty()

    val inboxIds: Set<SnapshotId>
        get() = snapshots.entries
            .filter { (_, doc) -> doc.isInboxSnapshot() }
            .map { it.key }
            .toSet()

    // ─── 변환 ────────────────────────────────────────────────────────

    /** 아직 String ID를 요구하는 레거시 코드와의 호환성을 위해 제공 */
    fun toMap(): Map<String, BookmarkDocument> = snapshots.entries.associate { it.key.value to it.value }

    companion object {
        fun of(orderedIds: List<String>, documents: Map<String, BookmarkDocument>): BookmarkSnapshots =
            BookmarkSnapshots(
                snapshots = LinkedHashMap<SnapshotId, BookmarkDocument>().also { map ->
                    orderedIds.forEach { id ->
                        val snapshotId = SnapshotId(id)
                        documents[id]?.let { map[snapshotId] = it }
                    }
                },
            )
    }
}
