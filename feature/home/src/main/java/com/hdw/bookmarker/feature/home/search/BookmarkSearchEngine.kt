package com.hdw.bookmarker.feature.home.search

import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.feature.home.search.model.BookmarkSearchItemType
import com.hdw.bookmarker.feature.home.search.model.BookmarkSearchResult

class BookmarkSearchEngine {
    fun search(
        query: String,
        orderedSnapshotIds: List<String>,
        bookmarkDocuments: Map<String, BookmarkDocument>,
        snapshotTitles: Map<String, String>,
    ): List<BookmarkSearchResult> {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isBlank()) return emptyList()

        return orderedSnapshotIds.flatMap { snapshotId ->
            val document = bookmarkDocuments[snapshotId] ?: return@flatMap emptyList()
            val snapshotTitle = snapshotTitles[snapshotId]
                ?.trim()
                ?.takeIf(String::isNotBlank)
                ?: snapshotId
            searchItems(
                snapshotId = snapshotId,
                snapshotTitle = snapshotTitle,
                items = document.rootItems,
                normalizedQuery = normalizedQuery,
            )
        }
    }

    private fun searchItems(
        snapshotId: String,
        snapshotTitle: String,
        items: List<BookmarkItem>,
        normalizedQuery: String,
        pathPrefix: List<Int> = emptyList(),
        parentFolderTitles: List<String> = emptyList(),
    ): List<BookmarkSearchResult> {
        val results = mutableListOf<BookmarkSearchResult>()

        items.forEachIndexed { index, item ->
            val itemPath = pathPrefix + index
            when (item) {
                is BookmarkItem.Folder -> {
                    if (
                        item.title.contains(normalizedQuery, ignoreCase = true) ||
                        item.description.orEmpty().contains(normalizedQuery, ignoreCase = true)
                    ) {
                        results += BookmarkSearchResult(
                            snapshotId = snapshotId,
                            snapshotTitle = snapshotTitle,
                            itemPath = itemPath,
                            revealFolderPath = itemPath,
                            itemType = BookmarkSearchItemType.FOLDER,
                            title = item.title,
                            secondaryText = item.description?.takeIf(String::isNotBlank),
                            breadcrumb = buildBreadcrumb(snapshotTitle, parentFolderTitles),
                        )
                    }
                    results += searchItems(
                        snapshotId = snapshotId,
                        snapshotTitle = snapshotTitle,
                        items = item.children,
                        normalizedQuery = normalizedQuery,
                        pathPrefix = itemPath,
                        parentFolderTitles = parentFolderTitles + item.title,
                    )
                }

                is BookmarkItem.Bookmark -> {
                    if (
                        item.title.contains(normalizedQuery, ignoreCase = true) ||
                        item.url.contains(normalizedQuery, ignoreCase = true) ||
                        item.note.orEmpty().contains(normalizedQuery, ignoreCase = true) ||
                        item.tags.any { tag -> tag.contains(normalizedQuery, ignoreCase = true) }
                    ) {
                        results += BookmarkSearchResult(
                            snapshotId = snapshotId,
                            snapshotTitle = snapshotTitle,
                            itemPath = itemPath,
                            revealFolderPath = pathPrefix.takeIf(List<Int>::isNotEmpty),
                            itemType = BookmarkSearchItemType.BOOKMARK,
                            title = item.title,
                            secondaryText = item.note?.takeIf(String::isNotBlank) ?: item.url,
                            breadcrumb = buildBreadcrumb(snapshotTitle, parentFolderTitles),
                            bookmarkUrl = item.url,
                            bookmarkIconUri = item.iconUri,
                        )
                    }
                }
            }
        }

        return results
    }

    private fun buildBreadcrumb(
        snapshotTitle: String,
        parentFolderTitles: List<String>,
    ): String = buildList {
        add(snapshotTitle)
        addAll(parentFolderTitles)
    }.joinToString(separator = " / ")
}
