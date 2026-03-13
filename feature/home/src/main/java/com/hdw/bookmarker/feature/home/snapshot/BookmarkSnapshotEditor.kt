package com.hdw.bookmarker.feature.home.snapshot

import com.hdw.bookmarker.core.domain.usecase.GetBookmarkSnapshotRawFileHashUseCase
import com.hdw.bookmarker.core.domain.usecase.SaveBookmarkSnapshotUseCase
import com.hdw.bookmarker.core.model.bookmark.BOOKMARK_DOCUMENT_KIND_INBOX
import com.hdw.bookmarker.core.model.bookmark.BOOKMARK_DOCUMENT_META_KIND
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.bookmark.isInboxSnapshot
import com.hdw.bookmarker.feature.home.contract.UpdateBookmarkItemRequest
import com.hdw.bookmarker.feature.home.editor.BookmarkTreeEditor
import javax.inject.Inject

class BookmarkSnapshotEditor @Inject constructor(
    private val getBookmarkSnapshotRawFileHashUseCase: GetBookmarkSnapshotRawFileHashUseCase,
    private val saveBookmarkSnapshotUseCase: SaveBookmarkSnapshotUseCase,
    private val bookmarkTreeEditor: BookmarkTreeEditor,
    private val snapshotTitleGenerator: SnapshotTitleGenerator,
) {
    suspend fun addEmptySnapshot(existingDocuments: Collection<BookmarkDocument>): String {
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

    suspend fun getOrCreateInboxSnapshot(
        bookmarkDocuments: Map<String, BookmarkDocument>,
    ): Pair<String?, BookmarkDocument> {
        val existingInbox = bookmarkDocuments.entries.firstOrNull { (_, document) ->
            document.isInboxSnapshot()
        }
        if (existingInbox != null) {
            return existingInbox.key to existingInbox.value
        }

        return null to BookmarkDocument(
            title = snapshotTitleGenerator.inboxTitle(),
            metas = mapOf(BOOKMARK_DOCUMENT_META_KIND to BOOKMARK_DOCUMENT_KIND_INBOX),
            rootItems = emptyList(),
        )
    }

    suspend fun addItemToInbox(
        bookmarkDocuments: Map<String, BookmarkDocument>,
        item: BookmarkItem,
    ): String {
        val (snapshotId, inboxDocument) = getOrCreateInboxSnapshot(bookmarkDocuments)
        return saveSnapshot(
            snapshotId = snapshotId,
            document = inboxDocument.copy(rootItems = inboxDocument.rootItems + item),
        )
    }

    suspend fun addItem(
        currentSnapshotId: String?,
        bookmarkDocuments: Map<String, BookmarkDocument>,
        item: BookmarkItem,
        parentFolderPath: List<Int>? = null,
    ): String {
        val currentDocument = currentSnapshotId
            ?.let(bookmarkDocuments::get)
            ?: BookmarkDocument(
                title = snapshotTitleGenerator.nextDefaultTitle(bookmarkDocuments.values),
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

    suspend fun deleteItem(
        snapshotId: String,
        document: BookmarkDocument,
        path: List<Int>,
    ): String? {
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
        snapshotId: String,
        document: BookmarkDocument,
        request: UpdateBookmarkItemRequest,
    ): String? {
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

    suspend fun renameSnapshot(
        snapshotId: String,
        document: BookmarkDocument,
        title: String,
    ): String = saveSnapshot(
        snapshotId = snapshotId,
        document = document.copy(title = title),
    )

    private suspend fun saveSnapshot(
        snapshotId: String?,
        document: BookmarkDocument,
    ): String {
        val sourceHash = snapshotId
            ?.let { currentSnapshotId ->
                getBookmarkSnapshotRawFileHashUseCase(currentSnapshotId)
            }
            .orEmpty()

        return saveBookmarkSnapshotUseCase(
            snapshotId = snapshotId,
            document = document,
            sourceHash = sourceHash,
        )
    }
}
