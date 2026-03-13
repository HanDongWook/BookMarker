package com.hdw.bookmarker.core.data.bookmark.parser

import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class BookmarkHtmlParser {

    fun getBookmarkDocument(html: String): BookmarkDocument {
        val doc: Document = Jsoup.parse(html)

        val title = doc.title()

        val metas = doc.select(HtmlTag.META).associate { meta ->
            meta.attr(HtmlAttribute.NAME) to meta.attr(HtmlAttribute.CONTENT)
        }.filterKeys { it.isNotBlank() }

        val rootDl = doc.selectFirst(HtmlTag.DEFINITION_LIST) ?: return BookmarkDocument(title, metas, emptyList())
        val items = parseDl(rootDl)

        return BookmarkDocument(title, metas, items)
    }

    private fun parseDl(dl: Element): List<BookmarkItem> {
        val items = mutableListOf<BookmarkItem>()
        val directNodes = unwrapParagraphChildren(dl)

        for (node in directNodes) {
            if (!node.tagName().equals(HtmlTag.DEFINITION_TERM, ignoreCase = true)) {
                continue
            }

            val directAnchor = node.children()
                .firstOrNull { it.tagName().equals(HtmlTag.ANCHOR, ignoreCase = true) }
            if (directAnchor != null) {
                items.add(
                    BookmarkItem.Bookmark(
                        title = directAnchor.text(),
                        url = directAnchor.attr(HtmlAttribute.HREF),
                        addDate = directAnchor.attr(HtmlAttribute.ADD_DATE).takeIf { it.isNotBlank() },
                        lastModified = directAnchor.attr(HtmlAttribute.LAST_MODIFIED).takeIf { it.isNotBlank() },
                        iconUri = directAnchor.attr(HtmlAttribute.ICON_URI).takeIf { it.isNotBlank() },
                    ),
                )
                continue
            }

            val directH3 = node.children()
                .firstOrNull { it.tagName().equals(HtmlTag.H3, ignoreCase = true) }
                ?: continue

            val nextSibling = node.nextElementSibling()
            val description = when {
                nextSibling?.tagName().equals(HtmlTag.DESCRIPTION, ignoreCase = true) -> {
                    nextSibling?.text()?.takeIf { it.isNotBlank() }
                }

                else -> null
            }
            val childDl = node.children()
                .firstOrNull { it.tagName().equals(HtmlTag.DEFINITION_LIST, ignoreCase = true) }
                ?: when {
                    nextSibling?.tagName().equals(HtmlTag.DESCRIPTION, ignoreCase = true) -> {
                        nextSibling?.nextElementSibling()
                            ?.takeIf { it.tagName().equals(HtmlTag.DEFINITION_LIST, ignoreCase = true) }
                    }

                    else ->
                        nextSibling
                            ?.takeIf { it.tagName().equals(HtmlTag.DEFINITION_LIST, ignoreCase = true) }
                }
                ?: node.nextElementSiblings()
                    .firstOrNull { it.tagName().equals(HtmlTag.DEFINITION_LIST, ignoreCase = true) }

            val children = childDl?.let { parseDl(it) } ?: emptyList()
            items.add(
                BookmarkItem.Folder(
                    title = directH3.text(),
                    description = description,
                    addDate = directH3.attr(HtmlAttribute.ADD_DATE).takeIf { it.isNotBlank() },
                    lastModified = directH3.attr(HtmlAttribute.LAST_MODIFIED).takeIf { it.isNotBlank() },
                    children = children,
                ),
            )
        }

        return items
    }

    private fun unwrapParagraphChildren(container: Element): List<Element> = container.children().flatMap { child ->
        if (child.tagName().equals(HtmlTag.PARAGRAPH, ignoreCase = true)) {
            unwrapParagraphChildren(child)
        } else {
            listOf(child)
        }
    }

    private object HtmlAttribute {
        const val NAME = "name"
        const val CONTENT = "content"
        const val HREF = "href"
        const val ADD_DATE = "add_date"
        const val LAST_MODIFIED = "last_modified"
        const val ICON_URI = "icon_uri"
    }

    private object HtmlTag {
        const val META = "meta"
        const val DEFINITION_LIST = "dl"
        const val DEFINITION_TERM = "dt"
        const val PARAGRAPH = "p"
        const val H3 = "h3"
        const val ANCHOR = "a"
        const val DESCRIPTION = "dd"
    }
}
