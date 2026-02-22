package com.hdw.bookmarker.core.datastore.bookmark

import com.hdw.bookmarker.core.datastore.proto.BookmarkDocumentProto
import com.hdw.bookmarker.core.datastore.proto.BookmarkFolderProto
import com.hdw.bookmarker.core.datastore.proto.BookmarkLinkProto
import com.hdw.bookmarker.core.datastore.proto.BookmarkNodeProto
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem

internal fun BookmarkDocument.toProto(): BookmarkDocumentProto = BookmarkDocumentProto(
    title = title.orEmpty(),
    metas = metas,
    rootItems = rootItems.map { it.toNodeProto() },
)

internal fun BookmarkDocumentProto.toModel(): BookmarkDocument = BookmarkDocument(
    title = title.takeIf { it.isNotBlank() },
    metas = metas,
    rootItems = rootItems.mapNotNull { it.toModel() },
)

private fun BookmarkItem.toNodeProto(): BookmarkNodeProto = when (this) {
    is BookmarkItem.Bookmark -> BookmarkNodeProto(
        bookmark = BookmarkLinkProto(
            title = title,
            url = url,
            addDate = addDate.orEmpty(),
            lastModified = lastModified.orEmpty(),
            iconUri = iconUri.orEmpty(),
        ),
    )

    is BookmarkItem.Folder -> BookmarkNodeProto(
        folder = BookmarkFolderProto(
            title = title,
            addDate = addDate.orEmpty(),
            lastModified = lastModified.orEmpty(),
            children = children.map { it.toNodeProto() },
        ),
    )
}

private fun BookmarkNodeProto.toModel(): BookmarkItem? = when {
    folder != null -> folder.toModel()
    bookmark != null -> bookmark.toModel()
    else -> null
}

private fun BookmarkFolderProto.toModel(): BookmarkItem.Folder = BookmarkItem.Folder(
    title = title,
    addDate = addDate.ifBlank { null },
    lastModified = lastModified.ifBlank { null },
    children = children.mapNotNull { it.toModel() },
)

private fun BookmarkLinkProto.toModel(): BookmarkItem.Bookmark = BookmarkItem.Bookmark(
    title = title,
    url = url,
    addDate = addDate.ifBlank { null },
    lastModified = lastModified.ifBlank { null },
    iconUri = iconUri.ifBlank { null },
)
