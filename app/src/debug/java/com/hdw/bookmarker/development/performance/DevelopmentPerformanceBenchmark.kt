package com.hdw.bookmarker.development.performance

import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import kotlinx.coroutines.flow.first
import kotlin.system.measureNanoTime

internal object DevelopmentPerformanceBenchmark {
    suspend fun run(bookmarkRepository: BookmarkRepository): String {
        val snapshotsHolder = mutableMapOf<String, BookmarkDocument>()
        val readNanos = measureNanoTime {
            snapshotsHolder.putAll(bookmarkRepository.getBookmarkSnapshotsFlow().first())
        }
        var itemCount = 0
        val traverseNanos = measureNanoTime {
            itemCount = snapshotsHolder.values.sumOf { it.rootItems.countItems() }
        }
        val benchmarkDoc = BookmarkDocument(
            title = "DEV Benchmark Temp",
            metas = emptyMap(),
            rootItems = listOf(
                BookmarkItem.Bookmark(
                    title = "Benchmark",
                    url = "https://example.com",
                    addDate = "0",
                    lastModified = "0",
                    iconUri = null,
                ),
            ),
        )
        var createdSnapshotId = ""
        val writeNanos = measureNanoTime {
            createdSnapshotId = bookmarkRepository.saveBookmarkSnapshot(
                snapshotId = null,
                document = benchmarkDoc,
                sourceHash = "debug-benchmark",
                bookmarkColor = 0xFF1E88E5,
            )
        }
        bookmarkRepository.clearBookmarkSnapshot(createdSnapshotId)

        return "Read %.1fms | Traverse %.1fms | Write %.1fms | Items %d".format(
            readNanos.toMillis(),
            traverseNanos.toMillis(),
            writeNanos.toMillis(),
            itemCount,
        )
    }

    private fun List<BookmarkItem>.countItems(): Int = sumOf { item ->
        when (item) {
            is BookmarkItem.Bookmark -> 1
            is BookmarkItem.Folder -> 1 + item.children.countItems()
        }
    }

    private fun Long.toMillis(): Double = this / 1_000_000.0
}
