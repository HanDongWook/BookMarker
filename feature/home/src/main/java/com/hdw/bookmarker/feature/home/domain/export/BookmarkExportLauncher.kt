package com.hdw.bookmarker.feature.home.domain.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.findActivity
import java.io.File

internal fun shareCurrentBookmarkTextExport(context: Context, bookmarkDocument: BookmarkDocument): Boolean {
    val fallbackTitle = context.getString(R.string.export_current_bookmarks_label)
    val subject = buildBookmarkExportTitle(bookmarkDocument, fallbackTitle)
    val shareText = buildBookmarkExportTextContent(bookmarkDocument, fallbackTitle) ?: return false

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    val chooser = Intent.createChooser(
        shareIntent,
        context.getString(R.string.export_current_bookmarks_chooser_title),
    )
    context.findActivity()?.startActivity(chooser)
    return true
}

internal fun shareCurrentBookmarkHtmlExport(context: Context, bookmarkDocument: BookmarkDocument): Boolean {
    return runCatching {
        val html = buildBookmarkExportHtmlContent(bookmarkDocument) ?: return false
        val fileName = buildBookmarkExportFileName(bookmarkDocument, extension = "html")
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
            context.getString(R.string.export_current_bookmarks_chooser_title),
        )
        context.findActivity()?.startActivity(chooser)
        true
    }.getOrDefault(false)
}

internal fun saveBookmarkExportContent(context: Context, uri: Uri, content: String): Boolean = runCatching {
    val outputStream = context.contentResolver.openOutputStream(uri) ?: return false
    outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
        writer.write(content)
    }
    true
}.getOrDefault(false)
