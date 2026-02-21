package com.hdw.bookmarker.core.model.bookmark.extensions

import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import timber.log.Timber

/**
 * DFS - 전위 순회
 */
fun BookmarkItem.dfsPreOrder(): Sequence<BookmarkItem> = sequence {
    Timber.e("dfsPreOrder this:${this@dfsPreOrder}")
    val bookmarkItem = this@dfsPreOrder
    yield(bookmarkItem)
    if (bookmarkItem.isFolder()) {
        val folder = (bookmarkItem as BookmarkItem.Folder)
        folder.children.forEach { child ->
            yieldAll(child.dfsPreOrder())
        }
    }
}

/**
 * DFS - 후위 순회
 */
fun BookmarkItem.dfsPostOrder(): Sequence<BookmarkItem> = sequence {
    val bookmarkItem = this@dfsPostOrder
    if (bookmarkItem.isFolder()) {
        val folder = (bookmarkItem as BookmarkItem.Folder)
        folder.children.forEach { child ->
            yieldAll(child.dfsPostOrder())
        }
    }
    yield(bookmarkItem)
}

/**
 * BFS
 */
fun BookmarkItem.bfs(): Sequence<BookmarkItem> = sequence {
    val queue = ArrayDeque<BookmarkItem>()
    queue.add(this@bfs)
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        yield(current)
        if (current is BookmarkItem.Folder) {
            queue.addAll(current.children)
        }
    }
}

fun Iterable<BookmarkItem>.dfsPreOrder(): Sequence<BookmarkItem> = asSequence().flatMap { it.dfsPreOrder() }
fun Iterable<BookmarkItem>.dfsPostOrder(): Sequence<BookmarkItem> = asSequence().flatMap { it.dfsPostOrder() }
fun Iterable<BookmarkItem>.bfs(): Sequence<BookmarkItem> = asSequence().flatMap { it.bfs() }
