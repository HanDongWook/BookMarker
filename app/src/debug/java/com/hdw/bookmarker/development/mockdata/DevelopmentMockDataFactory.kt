package com.hdw.bookmarker.development.mockdata

import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem

internal object DevelopmentMockDataFactory {
    fun buildMockDocument(): BookmarkDocument {
        val now = (System.currentTimeMillis() / 1000).toString()
        return BookmarkDocument(
            title = "DEV Mock Library",
            metas = mapOf("generated_by" to "development"),
            rootItems = listOf(
                BookmarkItem.Folder(
                    title = "Android",
                    description = "Android references",
                    addDate = now,
                    lastModified = now,
                    children = listOf(
                        BookmarkItem.Bookmark(
                            title = "Android Developers",
                            url = "https://developer.android.com",
                            addDate = now,
                            lastModified = now,
                            iconUri = null,
                            description = "Official Android docs",
                            tags = listOf("android", "docs"),
                        ),
                        BookmarkItem.Bookmark(
                            title = "Jetpack Compose",
                            url = "https://developer.android.com/jetpack/compose",
                            addDate = now,
                            lastModified = now,
                            iconUri = null,
                            description = "Compose guides",
                            tags = listOf("compose"),
                        ),
                    ),
                ),
                BookmarkItem.Folder(
                    title = "Kotlin",
                    description = "Kotlin ecosystem",
                    addDate = now,
                    lastModified = now,
                    children = listOf(
                        BookmarkItem.Bookmark(
                            title = "Kotlinlang",
                            url = "https://kotlinlang.org",
                            addDate = now,
                            lastModified = now,
                            iconUri = null,
                            tags = listOf("kotlin"),
                        ),
                        BookmarkItem.Folder(
                            title = "Libraries",
                            description = null,
                            addDate = now,
                            lastModified = now,
                            children = (1..10).map { index ->
                                BookmarkItem.Bookmark(
                                    title = "Library $index",
                                    url = "https://example.com/library/$index",
                                    addDate = now,
                                    lastModified = now,
                                    iconUri = null,
                                )
                            },
                        ),
                    ),
                ),
                BookmarkItem.Bookmark(
                    title = "BookMarker Repo",
                    url = "https://github.com",
                    addDate = now,
                    lastModified = now,
                    iconUri = null,
                    description = "Project source",
                    tags = listOf("project"),
                ),
            ),
        )
    }
}
