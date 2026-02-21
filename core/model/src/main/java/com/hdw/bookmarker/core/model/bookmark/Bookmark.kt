package com.hdw.bookmarker.core.model.bookmark

import com.hdw.bookmarker.core.model.browser.Browser

data class Bookmark(
    val id: Long,
    val title: String,
    val url: String? = null,
    val sourceBrowser: Browser = Browser.UNKNOWN,
    val type: Type = Type.BOOKMARK,
    val children: List<Bookmark> = emptyList(),
    val folderPath: List<String> = emptyList(),
    val metadata: Metadata = Metadata(),
) {
    val isFolder: Boolean
        get() = type == Type.FOLDER

    val folder: String?
        get() = folderPath.lastOrNull()

    val createdAt: Long?
        get() = metadata.addDateEpochSeconds

    fun dfs(): List<Bookmark> {
        val result = mutableListOf<Bookmark>()

        fun traverse(node: Bookmark) {
            result += node
            node.children.forEach(::traverse)
        }

        traverse(this)
        return result
    }

    fun bfs(): List<Bookmark> {
        val result = mutableListOf<Bookmark>()
        val queue = ArrayDeque<Bookmark>()
        queue.add(this)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            result += current
            current.children.forEach(queue::addLast)
        }

        return result
    }

    data class Metadata(
        val addDateEpochSeconds: Long? = null,
        val lastModifiedEpochSeconds: Long? = null,
        val iconUri: String? = null,
    )

    enum class Type {
        FOLDER,
        BOOKMARK,
    }
}
