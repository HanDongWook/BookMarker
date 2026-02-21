package com.hdw.bookmarker.core.data.bookmarker.chrome

import com.hdw.bookmarker.core.data.bookmark.chrome.ChromeBookmarkParser
import com.hdw.bookmarker.core.model.bookmark.Bookmark
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.bookmark.extensions.bfs
import com.hdw.bookmarker.core.model.bookmark.extensions.dfsPostOrder
import com.hdw.bookmarker.core.model.bookmark.extensions.dfsPreOrder
import com.hdw.bookmarker.core.model.browser.Browser
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ChromeBookmarkParserTest {
    @Test
    fun sampleBookmarkHtml_parse_string_returns_bookmark_item_tree() {
        val parser = ChromeBookmarkParser()
        val html = requireNotNull(
            ChromeBookmarkParserTest::class.java.getResourceAsStream("/sampleBookmark.html")
        ).bufferedReader().use { it.readText() }

        val items = parser.parse(html)
        items.shouldNotBeEmpty()

        val allNodes = items.flatMap { it.dfsPreOrder().toList() }
        allNodes.shouldNotBeEmpty()

        val bookmarkBar = allNodes
            .filterIsInstance<BookmarkItem.Folder>()
            .firstOrNull { it.title == "Bookmark Bar" }
        bookmarkBar.shouldNotBeNull()

        val cryptoFolder = allNodes
            .filterIsInstance<BookmarkItem.Folder>()
            .firstOrNull { it.title == "Crypto" }
        cryptoFolder.shouldNotBeNull()

        val dataFolder = allNodes
            .filterIsInstance<BookmarkItem.Folder>()
            .firstOrNull { it.title == "Data" }
        dataFolder.shouldNotBeNull()

        val kimchi = allNodes
            .filterIsInstance<BookmarkItem.Bookmark>()
            .firstOrNull { it.title == "김치프리미엄" }
        kimchi.shouldNotBeNull()
        kimchi.url shouldBe "https://scolkg.com/"
    }

    @Test
    fun parsed_bookmark_items_are_printed_by_bookmark_item_extensions() {
        val parser = ChromeBookmarkParser()
        val html = requireNotNull(
            ChromeBookmarkParserTest::class.java.getResourceAsStream("/sampleBookmark.html")
        ).bufferedReader().use { it.readText() }

        val items = parser.parse(html)
        items.shouldNotBeEmpty()
        println("items.size:${items.size}")

        val preOrderNodes = items.flatMap { it.dfsPreOrder().toList() }
        preOrderNodes.shouldNotBeEmpty()
        println("BookmarkItemTraversal DFS_PRE_ORDER START size:${preOrderNodes.size}")
        preOrderNodes.forEach { println("DFS_PRE -> ${it.printable()}") }
        println("BookmarkItemTraversal DFS_PRE_ORDER END")

        val postOrderNodes = items.flatMap { it.dfsPostOrder().toList() }
        postOrderNodes.shouldNotBeEmpty()
        println("BookmarkItemTraversal DFS_POST_ORDER START size:${postOrderNodes.size}")
        postOrderNodes.forEach { println("DFS_POST -> ${it.printable()}") }
        println("BookmarkItemTraversal DFS_POST_ORDER END")

        val bfsNodes = items.flatMap { it.bfs().toList() }
        bfsNodes.shouldNotBeEmpty()
        println("BookmarkItemTraversal BFS START size:${bfsNodes.size}")
        bfsNodes.forEach { println("BFS -> ${it.printable()}") }
        println("BookmarkItemTraversal BFS END")
    }

    @Test
    fun sampleBookmarkHtml_is_parsed_into_bookmark_list() = runTest {
        val bookmarkParser = ChromeBookmarkParser()
        val inputStream = requireNotNull(
            ChromeBookmarkParserTest::class.java.getResourceAsStream("/sampleBookmark.html")
        )

        val bookmarks = inputStream.use { bookmarkParser.parseHtml(it) }
        println("BookmarkParserTest: parsed bookmark count = ${bookmarks.size}")

        bookmarks.shouldNotBeEmpty()
        for (bookmark in bookmarks.first().bfs()) {
            println("BookmarkParserTest: title='${bookmark.title}', url=${bookmark.url}")
        }

        val target = bookmarks.firstOrNull { it.title == "번역" }
        target.shouldNotBeNull()
        println("BookmarkParserTest: target='${target.title}', folderPath=${target.folderPath}")
        target.url.shouldNotBeNull()
        target.url.shouldContain("translate.google.com")
        target.folder shouldBe "Bookmark Bar"
        target.sourceBrowser.name shouldBe "CHROME"
        target.metadata.addDateEpochSeconds.shouldNotBeNull()
        target.metadata.lastModifiedEpochSeconds.shouldNotBeNull()
        target.metadata.iconUri.shouldNotBeNull()
    }

    @Test
    fun sampleBookmarkHtml_nested_folder_path_and_metadata_are_parsed() = runTest {
        val parser = ChromeBookmarkParser()
        val inputStream = requireNotNull(
            ChromeBookmarkParserTest::class.java.getResourceAsStream("/sampleBookmark.html")
        )

        val bookmarks = inputStream.use { parser.parseHtml(it) }
        val nested = bookmarks.firstOrNull { it.title == "김치프리미엄" }
        nested.shouldNotBeNull()
        println(
            "BookmarkParserTest: nested='${nested.title}', " +
                "path=${nested.folderPath}, addDate=${nested.metadata.addDateEpochSeconds}"
        )

        nested.folderPath.firstOrNull() shouldBe "Bookmark Bar"
        nested.folderPath.shouldBe(listOf("Bookmark Bar", "Crypto", "Data"))
        nested.url.shouldNotBeNull()
        nested.url shouldBe "https://scolkg.com/"
        nested.metadata.lastModifiedEpochSeconds.shouldNotBeNull()
    }

    @Test
    fun bookmark_tree_dfs_and_bfs_print_each_node() {
        val root = Bookmark(
            id = 1L,
            title = "Bookmark Bar",
            type = Bookmark.Type.FOLDER,
            sourceBrowser = Browser.CHROME,
            children = listOf(
                Bookmark(
                    id = 2L,
                    title = "Crypto",
                    type = Bookmark.Type.FOLDER,
                    sourceBrowser = Browser.CHROME,
                    children = listOf(
                        Bookmark(
                            id = 3L,
                            title = "Data",
                            type = Bookmark.Type.FOLDER,
                            sourceBrowser = Browser.CHROME,
                            children = listOf(
                                Bookmark(
                                    id = 4L,
                                    title = "김치프리미엄",
                                    url = "https://scolkg.com/",
                                    sourceBrowser = Browser.CHROME
                                )
                            )
                        ),
                        Bookmark(
                            id = 5L,
                            title = "DefiLlama",
                            url = "https://defillama.com/",
                            sourceBrowser = Browser.CHROME
                        )
                    )
                ),
                Bookmark(
                    id = 6L,
                    title = "번역",
                    url = "https://translate.google.com/",
                    sourceBrowser = Browser.CHROME
                )
            )
        )

        println("BookmarkTraversal DFS START")
        root.dfs().forEach { node ->
            println("DFS -> title='${node.title}', type=${node.type}, url=${node.url}")
        }
        println("BookmarkTraversal DFS END")

        println("BookmarkTraversal BFS START")
        root.bfs().forEach { node ->
            println("BFS -> title='${node.title}', type=${node.type}, url=${node.url}")
        }
        println("BookmarkTraversal BFS END")
    }

    private fun BookmarkItem.printable(): String = when (this) {
        is BookmarkItem.Bookmark -> "bookmark title='${title}', url='${url}'"
        is BookmarkItem.Folder -> "folder title='${title}', children=${children.size}"
    }
}
