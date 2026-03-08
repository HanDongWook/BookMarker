package com.hdw.bookmarker.feature.home.ui.share
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.findActivity
import java.io.File

internal fun requestCurrentBookmarkTextShare(context: Context, bookmarkDocument: BookmarkDocument): Boolean {
    val entries = flattenBookmarkEntries(bookmarkDocument.rootItems)
    if (entries.isEmpty()) return false

    val subject = bookmarkDocument.title
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?: context.getString(R.string.share_current_bookmarks_label)

    val shareText = buildString {
        append(subject)
        append("\n\n")
        entries.forEachIndexed { index, entry ->
            append("${index + 1}. ${entry.displayTitle}\n")
            append(entry.url)
            if (index != entries.lastIndex) {
                append("\n\n")
            }
        }
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    val chooser = Intent.createChooser(
        shareIntent,
        context.getString(R.string.share_current_bookmarks_chooser_title),
    )
    context.findActivity()?.startActivity(chooser)
    return true
}

internal fun requestCurrentBookmarkHtmlShare(context: Context, bookmarkDocument: BookmarkDocument): Boolean {
    val entries = flattenBookmarkEntries(bookmarkDocument.rootItems)
    if (entries.isEmpty()) return false

    return runCatching {
        val html = buildBookmarkHtml(bookmarkDocument)
        val fileName = buildFileName(bookmarkDocument)
        val exportDir = File(context.cacheDir, "shared_bookmarks").apply { mkdirs() }
        val exportFile = File(exportDir, fileName)
        exportFile.writeText(html, Charsets.UTF_8)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            exportFile,
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/html"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(
            shareIntent,
            context.getString(R.string.share_current_bookmarks_chooser_title),
        )
        context.findActivity()?.startActivity(chooser)
        true
    }.getOrDefault(false)
}

private data class ShareBookmarkEntry(val displayTitle: String, val url: String)

private fun flattenBookmarkEntries(
    items: List<BookmarkItem>,
    folderPath: List<String> = emptyList(),
): List<ShareBookmarkEntry> = items.flatMap { item ->
    when (item) {
        is BookmarkItem.Folder -> flattenBookmarkEntries(
            items = item.children,
            folderPath = folderPath + item.title,
        )

        is BookmarkItem.Bookmark -> {
            val prefix = folderPath.joinToString(separator = " / ")
            val displayTitle = if (prefix.isBlank()) item.title else "$prefix / ${item.title}"
            listOf(ShareBookmarkEntry(displayTitle = displayTitle, url = item.url))
        }
    }
}

private fun buildBookmarkHtml(bookmarkDocument: BookmarkDocument): String = buildString {
    appendLine("<!DOCTYPE NETSCAPE-Bookmark-file-1>")
    appendLine("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">")
    appendLine("<TITLE>${escapeHtml(bookmarkDocument.title.orEmpty())}</TITLE>")
    appendLine("<H1>${escapeHtml(bookmarkDocument.title.orEmpty())}</H1>")
    appendLine("<DL><p>")
    appendBookmarkItems(items = bookmarkDocument.rootItems, depth = 1)
    appendLine("</DL><p>")
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
                appendLine("$indent<DL><p>")
                appendBookmarkItems(item.children, depth + 1)
                appendLine("$indent</DL><p>")
            }
        }
    }
}

private fun buildFileName(bookmarkDocument: BookmarkDocument): String {
    val base = bookmarkDocument.title
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?: "bookmarks"
    val sanitized = base
        .replace(Regex("[^a-zA-Z0-9가-힣._-]"), "_")
        .take(40)
        .ifBlank { "bookmarks" }
    return "$sanitized.html"
}

private fun escapeHtml(value: String): String = value
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
