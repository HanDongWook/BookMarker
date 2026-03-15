package com.hdw.bookmarker.feature.home.presentation.component.bookmark.export

import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem

private const val DEFAULT_BOOKMARK_EXPORT_TITLE = "bookmarks"

internal fun buildBookmarkExportTitle(
    bookmarkDocument: BookmarkDocument,
    fallbackTitle: String = DEFAULT_BOOKMARK_EXPORT_TITLE,
): String = bookmarkDocument.title
    ?.trim()
    ?.takeIf { it.isNotBlank() }
    ?: fallbackTitle

internal fun buildBookmarkExportTextContent(
    bookmarkDocument: BookmarkDocument,
    fallbackTitle: String = DEFAULT_BOOKMARK_EXPORT_TITLE,
): String? {
    val entries = flattenBookmarkExportEntries(bookmarkDocument.rootItems)
    if (entries.isEmpty()) return null

    val title = buildBookmarkExportTitle(bookmarkDocument, fallbackTitle)
    return buildString {
        append(title)
        append("\n\n")
        entries.forEachIndexed { index, entry ->
            append("${index + 1}. ${entry.displayTitle}\n")
            append(entry.url)
            if (index != entries.lastIndex) {
                append("\n\n")
            }
        }
    }
}

internal fun buildBookmarkExportHtmlContent(bookmarkDocument: BookmarkDocument): String? {
    val entries = flattenBookmarkExportEntries(bookmarkDocument.rootItems)
    if (entries.isEmpty()) return null

    return buildString {
        appendLine("<!DOCTYPE NETSCAPE-Bookmark-file-1>")
        appendLine("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">")
        appendLine("<TITLE>${escapeHtml(bookmarkDocument.title.orEmpty())}</TITLE>")
        appendLine("<H1>${escapeHtml(bookmarkDocument.title.orEmpty())}</H1>")
        appendLine("<DL><p>")
        appendBookmarkItems(items = bookmarkDocument.rootItems, depth = 1)
        appendLine("</DL><p>")
    }
}

internal fun buildBookmarkExportFileName(bookmarkDocument: BookmarkDocument, extension: String): String {
    val base = buildBookmarkExportTitle(bookmarkDocument)
    val sanitized = base
        .replace(Regex("[^a-zA-Z0-9가-힣._-]"), "_")
        .take(40)
        .ifBlank { DEFAULT_BOOKMARK_EXPORT_TITLE }
    return "$sanitized.$extension"
}

private data class BookmarkExportEntry(val displayTitle: String, val url: String)

private fun flattenBookmarkExportEntries(
    items: List<BookmarkItem>,
    folderPath: List<String> = emptyList(),
): List<BookmarkExportEntry> = items.flatMap { item ->
    when (item) {
        is BookmarkItem.Folder -> flattenBookmarkExportEntries(
            items = item.children,
            folderPath = folderPath + item.title,
        )

        is BookmarkItem.Bookmark -> {
            val prefix = folderPath.joinToString(separator = " / ")
            val displayTitle = if (prefix.isBlank()) item.title else "$prefix / ${item.title}"
            listOf(BookmarkExportEntry(displayTitle = displayTitle, url = item.url))
        }
    }
}

private fun StringBuilder.appendBookmarkItems(items: List<BookmarkItem>, depth: Int) {
    val indent = "    ".repeat(depth)
    items.forEach { item ->
        when (item) {
            is BookmarkItem.Bookmark -> {
                appendLine("$indent<DT><A HREF=\"${escapeHtml(item.url)}\">${escapeHtml(item.title)}</A>")
            }

            is BookmarkItem.Folder -> {
                appendLine("$indent<DT><H3>${escapeHtml(item.title)}</H3>")
                item.description
                    ?.takeIf { it.isNotBlank() }
                    ?.let { description ->
                        appendLine("$indent<DD>${escapeHtml(description)}</DD>")
                    }
                appendLine("$indent<DL><p>")
                appendBookmarkItems(item.children, depth + 1)
                appendLine("$indent</DL><p>")
            }
        }
    }
}

private fun escapeHtml(value: String): String = value
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
