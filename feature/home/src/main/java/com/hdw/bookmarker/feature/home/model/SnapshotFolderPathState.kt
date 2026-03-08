package com.hdw.bookmarker.feature.home.model

data class SnapshotFolderPathState(private val pathsBySnapshotId: Map<String, List<Int>> = emptyMap()) {
    fun pathOf(snapshotId: String?): List<Int>? = snapshotId?.let(pathsBySnapshotId::get)

    fun update(snapshotId: String, path: List<Int>?): SnapshotFolderPathState {
        val updated = pathsBySnapshotId.toMutableMap()
        if (path.isNullOrEmpty()) {
            updated.remove(snapshotId)
        } else {
            updated[snapshotId] = path
        }
        return copy(pathsBySnapshotId = updated)
    }

    fun remove(snapshotId: String): SnapshotFolderPathState = copy(
        pathsBySnapshotId = pathsBySnapshotId - snapshotId,
    )

    fun retain(validSnapshotIds: Collection<String>): SnapshotFolderPathState = copy(
        pathsBySnapshotId = pathsBySnapshotId.filterKeys { it in validSnapshotIds },
    )
}
