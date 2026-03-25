package com.hdw.bookmarker.feature.home.domain.snapshot

import com.hdw.bookmarker.core.domain.usecase.GetBookmarkSnapshotRawFileHashUseCase
import com.hdw.bookmarker.core.domain.usecase.SaveBookmarkSnapshotUseCase
import com.hdw.bookmarker.core.model.bookmark.BOOKMARK_DOCUMENT_KIND_INBOX
import com.hdw.bookmarker.core.model.bookmark.BOOKMARK_DOCUMENT_META_KIND
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.feature.home.domain.model.BookmarkSnapshots
import com.hdw.bookmarker.feature.home.domain.model.UpdateBookmarkItemRequest
import com.hdw.bookmarker.feature.home.domain.tree.BookmarkTreeEditor
import javax.inject.Inject

class BookmarkSnapshotEditor @Inject constructor(
    private val getBookmarkSnapshotRawFileHashUseCase: GetBookmarkSnapshotRawFileHashUseCase,
    private val saveBookmarkSnapshotUseCase: SaveBookmarkSnapshotUseCase,
    private val bookmarkTreeEditor: BookmarkTreeEditor,
    private val snapshotTitleGenerator: SnapshotTitleGenerator,
) {
    suspend fun addEmptySnapshot(existingDocuments: Collection<BookmarkDocument>): SnapshotId {
        val snapshotTitle = snapshotTitleGenerator.nextDefaultTitle(existingDocuments)
        return saveSnapshot(
            snapshotId = null,
            document = BookmarkDocument(
                title = snapshotTitle,
                metas = emptyMap(),
                rootItems = emptyList(),
            ),
        )
    }

    suspend fun getOrCreateInboxSnapshot(library: BookmarkSnapshots): Pair<SnapshotId?, BookmarkDocument> {
        val existingInboxId = library.inboxIds.firstOrNull()
        if (existingInboxId != null) {
            return existingInboxId to library[existingInboxId]!!
        }

        return null to BookmarkDocument(
            title = snapshotTitleGenerator.inboxTitle(),
            metas = mapOf(BOOKMARK_DOCUMENT_META_KIND to BOOKMARK_DOCUMENT_KIND_INBOX),
            rootItems = emptyList(),
        )
    }

    suspend fun addItemToInbox(library: BookmarkSnapshots, item: BookmarkItem): SnapshotId {
        val (snapshotId, inboxDocument) = getOrCreateInboxSnapshot(library)
        return saveSnapshot(
            snapshotId = snapshotId,
            document = inboxDocument.copy(rootItems = inboxDocument.rootItems + item),
        )
    }

    suspend fun addItem(
        currentSnapshotId: SnapshotId?,
        library: BookmarkSnapshots,
        item: BookmarkItem,
        parentFolderPath: List<Int>? = null,
    ): SnapshotId {
        val currentDocument = currentSnapshotId
            ?.let(library::get)
            ?: BookmarkDocument(
                title = snapshotTitleGenerator.nextDefaultTitle(library.values),
                metas = emptyMap(),
                rootItems = emptyList(),
            )

        val updatedRootItems = if (parentFolderPath.isNullOrEmpty()) {
            currentDocument.rootItems + item
        } else {
            bookmarkTreeEditor.addItemToFolderByPath(
                items = currentDocument.rootItems,
                path = parentFolderPath,
                item = item,
            ) ?: (currentDocument.rootItems + item)
        }

        return saveSnapshot(
            snapshotId = currentSnapshotId,
            document = currentDocument.copy(rootItems = updatedRootItems),
        )
    }

    suspend fun deleteItem(snapshotId: SnapshotId, document: BookmarkDocument, path: List<Int>): SnapshotId? {
        val updatedRootItems = bookmarkTreeEditor.removeItemByPath(
            items = document.rootItems,
            path = path,
        ) ?: return null

        return saveSnapshot(
            snapshotId = snapshotId,
            document = document.copy(rootItems = updatedRootItems),
        )
    }

    suspend fun updateItem(
        snapshotId: SnapshotId,
        document: BookmarkDocument,
        request: UpdateBookmarkItemRequest,
    ): SnapshotId? {
        val updatedRootItems = bookmarkTreeEditor.updateItemByPath(
            items = document.rootItems,
            path = request.path,
            request = request,
        ) ?: return null

        return saveSnapshot(
            snapshotId = snapshotId,
            document = document.copy(rootItems = updatedRootItems),
        )
    }

    suspend fun renameSnapshot(snapshotId: SnapshotId, document: BookmarkDocument, title: String): SnapshotId =
        saveSnapshot(
            snapshotId = snapshotId,
            document = document.copy(title = title),
        )

    suspend fun moveItem(
        sourceSnapshotId: SnapshotId,
        sourceDocument: BookmarkDocument,
        sourcePath: List<Int>,
        targetSnapshotId: SnapshotId,
        targetDocument: BookmarkDocument,
        targetFolderPath: List<Int>? = null,
    ): SnapshotId? {
        val itemToMove = bookmarkTreeEditor.getItemByPath(
            items = sourceDocument.rootItems,
            path = sourcePath,
        ) ?: return null

        val updatedSourceRootItems = bookmarkTreeEditor.removeItemByPath(
            items = sourceDocument.rootItems,
            path = sourcePath,
        ) ?: return null

        val updatedTargetRootItems = if (targetFolderPath.isNullOrEmpty()) {
            targetDocument.rootItems + itemToMove
        } else {
            bookmarkTreeEditor.addItemToFolderByPath(
                items = targetDocument.rootItems,
                path = targetFolderPath,
                item = itemToMove,
            ) ?: (targetDocument.rootItems + itemToMove)
        }

        saveSnapshot(
            snapshotId = sourceSnapshotId,
            document = sourceDocument.copy(rootItems = updatedSourceRootItems),
        )
        return saveSnapshot(
            snapshotId = targetSnapshotId,
            document = targetDocument.copy(rootItems = updatedTargetRootItems),
        )
    }

    private suspend fun saveSnapshot(snapshotId: SnapshotId?, document: BookmarkDocument): SnapshotId {
        val sourceHash = snapshotId
            ?.let { currentSnapshotId ->
                getBookmarkSnapshotRawFileHashUseCase(currentSnapshotId.value)
            }
            .orEmpty()

        val savedId = saveBookmarkSnapshotUseCase(
            snapshotId = snapshotId?.value,
            document = document,
            sourceHash = sourceHash,
        )
        return SnapshotId(savedId)
    }
}
