package com.hdw.bookmarker.feature.home.domain.tree

import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.feature.home.domain.model.UpdateBookmarkItemRequest
import javax.inject.Inject

class BookmarkTreeEditor @Inject constructor() {
    fun getItemByPath(items: List<BookmarkItem>, path: List<Int>): BookmarkItem? {
        val targetIndex = path.firstOrNull() ?: return null
        if (targetIndex !in items.indices) return null

        val targetItem = items[targetIndex]
        if (path.size == 1) {
            return targetItem
        }

        val targetFolder = targetItem as? BookmarkItem.Folder ?: return null
        return getItemByPath(targetFolder.children, path.drop(1))
    }

    fun removeItemByPath(items: List<BookmarkItem>, path: List<Int>): List<BookmarkItem>? {
        val targetIndex = path.firstOrNull() ?: return null
        if (targetIndex !in items.indices) return null

        if (path.size == 1) {
            return items.toMutableList().apply { removeAt(targetIndex) }
        }

        val targetFolder = items[targetIndex] as? BookmarkItem.Folder ?: return null
        val updatedChildren = removeItemByPath(targetFolder.children, path.drop(1)) ?: return null

        return items.toMutableList().apply {
            this[targetIndex] = targetFolder.copy(children = updatedChildren)
        }
    }

    fun addItemToFolderByPath(items: List<BookmarkItem>, path: List<Int>, item: BookmarkItem): List<BookmarkItem>? {
        val targetIndex = path.firstOrNull() ?: return null
        if (targetIndex !in items.indices) return null

        val targetFolder = items[targetIndex] as? BookmarkItem.Folder ?: return null
        val updatedFolder = if (path.size == 1) {
            targetFolder.copy(children = targetFolder.children + item)
        } else {
            val updatedChildren = addItemToFolderByPath(targetFolder.children, path.drop(1), item) ?: return null
            targetFolder.copy(children = updatedChildren)
        }

        return items.toMutableList().apply {
            this[targetIndex] = updatedFolder
        }
    }

    fun updateItemByPath(
        items: List<BookmarkItem>,
        path: List<Int>,
        request: UpdateBookmarkItemRequest,
    ): List<BookmarkItem>? {
        val targetIndex = path.firstOrNull() ?: return null
        if (targetIndex !in items.indices) return null

        if (path.size == 1) {
            val now = currentEpochSecondsString()
            val target = items[targetIndex]
            val updatedItem = when {
                target is BookmarkItem.Folder && request is UpdateBookmarkItemRequest.Folder -> target.copy(
                    title = request.title,
                    description = request.description.trim().takeIf { it.isNotBlank() },
                    lastModified = now,
                )

                target is BookmarkItem.Bookmark && request is UpdateBookmarkItemRequest.Bookmark -> {
                    val trimmedUrl = request.url.trim()
                    if (trimmedUrl.isBlank()) return null
                    target.copy(
                        title = request.title,
                        url = normalizeUrl(trimmedUrl),
                        description = request.description.trim().takeIf { it.isNotBlank() },
                        tags = normalizeTags(request.tags),
                        lastModified = now,
                    )
                }

                else -> return null
            }

            return items.toMutableList().apply {
                this[targetIndex] = updatedItem
            }
        }

        val targetFolder = items[targetIndex] as? BookmarkItem.Folder ?: return null
        val updatedChildren = updateItemByPath(
            items = targetFolder.children,
            path = path.drop(1),
            request = request,
        ) ?: return null

        return items.toMutableList().apply {
            this[targetIndex] = targetFolder.copy(children = updatedChildren)
        }
    }

    private fun normalizeUrl(url: String): String {
        val trimmedUrl = url.trim()
        return if (trimmedUrl.contains("://")) trimmedUrl else "https://$trimmedUrl"
    }

    private fun normalizeTags(tags: List<String>): List<String> = tags
        .map(String::trim)
        .filter(String::isNotBlank)
        .distinct()

    private fun currentEpochSecondsString(): String = (System.currentTimeMillis() / 1000L).toString()
}
