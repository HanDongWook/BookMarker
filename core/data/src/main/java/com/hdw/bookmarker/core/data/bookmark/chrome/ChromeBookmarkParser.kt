package com.hdw.bookmarker.core.data.bookmark.chrome

import com.hdw.bookmarker.core.data.bookmark.BookmarkParser
import com.hdw.bookmarker.core.data.bookmark.util.Attribute
import com.hdw.bookmarker.core.data.bookmark.util.Tag
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ChromeBookmarkParser : BookmarkParser {

    override fun getBookmarkDocument(html: String): BookmarkDocument {
        val doc: Document = Jsoup.parse(html)

        val title = doc.title()

        val metas = doc.select("meta").associate { meta ->
            meta.attr("name") to meta.attr("content")
        }.filterKeys { it.isNotBlank() }

        val rootDl = doc.selectFirst(Tag.DEFINITION_LIST) ?: return BookmarkDocument(title, metas, emptyList())
        val items = parseDl(rootDl)

        return BookmarkDocument(title, metas, items)
    }

    private fun parseDl(dl: Element): List<BookmarkItem> {
        val items = mutableListOf<BookmarkItem>()
        val directNodes = unwrapParagraphChildren(dl)

        for (node in directNodes) {
            if (!node.tagName().equals(Tag.DEFINITION_TERM, ignoreCase = true)) {
                continue
            }

            val directAnchor = node.children()
                .firstOrNull { it.tagName().equals(Tag.ANCHOR, ignoreCase = true) }
            if (directAnchor != null) {
                items.add(
                    BookmarkItem.Bookmark(
                        title = directAnchor.text(),
                        url = directAnchor.attr(Attribute.HREF),
                        addDate = directAnchor.attr(Attribute.ADD_DATE).takeIf { it.isNotBlank() },
                        lastModified = directAnchor.attr(Attribute.LAST_MODIFIED).takeIf { it.isNotBlank() },
                        iconUri = directAnchor.attr(Attribute.ICON_URI).takeIf { it.isNotBlank() },
                    ),
                )
                continue
            }

            val directH3 = node.children()
                .firstOrNull { it.tagName().equals(Tag.H3, ignoreCase = true) }
                ?: continue

            val childDl = node.children()
                .firstOrNull { it.tagName().equals(Tag.DEFINITION_LIST, ignoreCase = true) }
                ?: node.nextElementSiblings()
                    .firstOrNull { it.tagName().equals(Tag.DEFINITION_LIST, ignoreCase = true) }

            val children = childDl?.let { parseDl(it) } ?: emptyList()
            items.add(
                BookmarkItem.Folder(
                    title = directH3.text(),
                    addDate = directH3.attr(Attribute.ADD_DATE).takeIf { it.isNotBlank() },
                    lastModified = directH3.attr(Attribute.LAST_MODIFIED).takeIf { it.isNotBlank() },
                    children = children,
                ),
            )
        }

        return items
    }

    private fun unwrapParagraphChildren(container: Element): List<Element> = container.children().flatMap { child ->
        if (child.tagName().equals(Tag.PARAGRAPH, ignoreCase = true)) {
            unwrapParagraphChildren(child)
        } else {
            listOf(child)
        }
    }
}
