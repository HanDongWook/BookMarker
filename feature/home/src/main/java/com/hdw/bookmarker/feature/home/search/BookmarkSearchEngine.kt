package com.hdw.bookmarker.feature.home.search

import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.feature.home.contract.BookmarkSnapshots
import com.hdw.bookmarker.feature.home.search.model.BookmarkSearchItemType
import com.hdw.bookmarker.feature.home.search.model.BookmarkSearchResult

class BookmarkSearchEngine {
    fun search(
        query: String,
        library: BookmarkSnapshots,
        snapshotTitles: Map<SnapshotId, String>,
    ): List<BookmarkSearchResult> {
        val normalizedQuery = query.trim().lowercase()
        if (normalizedQuery.isBlank()) return emptyList()

        val tagSearchQuery = normalizedQuery.removePrefix("#")

        return library.orderedIds.flatMap { snapshotId ->
            val document = library[snapshotId] ?: return@flatMap emptyList()
            val snapshotTitle = snapshotTitles[snapshotId]
                ?.trim()
                ?.takeIf(String::isNotBlank)
                ?: document.title
            searchItems(
                snapshotId = snapshotId,
                snapshotTitle = snapshotTitle.orEmpty(),
                items = document.rootItems,
                normalizedQuery = normalizedQuery,
                tagSearchQuery = tagSearchQuery,
            )
        }
    }

    private fun searchItems(
        snapshotId: SnapshotId,
        snapshotTitle: String,
        items: List<BookmarkItem>,
        normalizedQuery: String,
        tagSearchQuery: String,
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
                        tagSearchQuery = tagSearchQuery,
                        pathPrefix = itemPath,
                        parentFolderTitles = parentFolderTitles + item.title,
                    )
                }

                is BookmarkItem.Bookmark -> {
                    if (
                        item.title.contains(normalizedQuery, ignoreCase = true) ||
                        item.url.contains(normalizedQuery, ignoreCase = true) ||
                        item.description.orEmpty().contains(normalizedQuery, ignoreCase = true) ||
                        item.tags.any { tag -> tag.contains(tagSearchQuery, ignoreCase = true) }
                    ) {
                        results += BookmarkSearchResult(
                            snapshotId = snapshotId,
                            snapshotTitle = snapshotTitle,
                            itemPath = itemPath,
                            revealFolderPath = pathPrefix.takeIf(List<Int>::isNotEmpty),
                            itemType = BookmarkSearchItemType.BOOKMARK,
                            title = item.title,
                            secondaryText = item.description?.takeIf(String::isNotBlank) ?: item.url,
                            breadcrumb = buildBreadcrumb(snapshotTitle, parentFolderTitles),
                            bookmarkUrl = item.url,
                            bookmarkIconUri = item.iconUri,
                            tags = item.tags,
                        )
                    }
                }
            }
        }

        return results
    }

    private fun buildBreadcrumb(snapshotTitle: String, parentFolderTitles: List<String>): String = buildList {
        add(snapshotTitle)
        addAll(parentFolderTitles)
    }.joinToString(separator = " / ")
}
