package com.hdw.bookmarker.core.ui.util

import android.content.Context
import android.text.format.Formatter
import java.io.File

fun Context.getTemporaryDataSizeDisplay(): String {
    val sizeInBytes = temporaryDataDirectories().sumOf { it.safeDirectorySize() }
    return Formatter.formatShortFileSize(this, sizeInBytes)
}

fun Context.clearTemporaryData() {
    temporaryDataDirectories().forEach { it.clearDirectoryContents() }
}

private fun Context.temporaryDataDirectories(): List<File> = buildList {
    add(cacheDir)
    add(codeCacheDir)
    externalCacheDirs.filterNotNull().forEach(::add)
}.distinctBy { it.absolutePath }

private fun File.safeDirectorySize(): Long {
    if (!exists()) return 0L
    if (isFile) return length()

    val children = listFiles() ?: return 0L
    return children.sumOf { it.safeDirectorySize() }
}

private fun File.clearDirectoryContents() {
    if (!exists() || !isDirectory) return

    listFiles()?.forEach { child ->
        runCatching {
            if (child.isDirectory) {
                child.deleteRecursively()
            } else {
                child.delete()
            }
        }
    }
}
