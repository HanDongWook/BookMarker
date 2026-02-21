package com.hdw.bookmarker.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import androidx.core.net.toUri

@Composable
fun BookmarkContent(
    bookmarkDocument: BookmarkDocument?,
    modifier: Modifier = Modifier
) {
    if (bookmarkDocument == null || bookmarkDocument.rootItems.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = stringResource(R.string.home_no_bookmarks_imported))
        }
        return
    }

    val expandedFolders = rememberSaveable(saver = ExpandedFoldersSaver) {
        mutableStateMapOf<String, Boolean>()
    }
    val visibleNodes = remember(bookmarkDocument, expandedFolders.toMap()) {
        flattenBookmarkTree(
            items = bookmarkDocument.rootItems,
            expandedFolders = expandedFolders
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(start = 2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(items = visibleNodes, key = { it.key }) { node ->
            when (val item = node.item) {
                is BookmarkItem.Folder -> {
                    BookmarkFolderRow(
                        folder = item,
                        depth = node.depth,
                        isExpanded = expandedFolders[node.key] == true,
                        onToggle = {
                            if (expandedFolders[node.key] == true) {
                                expandedFolders.remove(node.key)
                            } else {
                                expandedFolders[node.key] = true
                            }
                        }
                    )
                }

                is BookmarkItem.Bookmark -> {
                    BookmarkLeafRow(
                        bookmark = item,
                        depth = node.depth
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarkFolderRow(
    folder: BookmarkItem.Folder,
    depth: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(start = (depth * 16).dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = folder.title,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f)
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null
        )
    }
}

@Composable
private fun BookmarkLeafRow(
    bookmark: BookmarkItem.Bookmark,
    depth: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (depth * 16).dp + 8.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BookmarkSiteImage(
            iconUri = bookmark.iconUri,
            url = bookmark.url,
            title = bookmark.title
        )
        Text(
            text = bookmark.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Composable
private fun BookmarkSiteImage(
    iconUri: String?,
    url: String,
    title: String
) {
    val candidateUrl = remember(iconUri, url) {
        iconUri
            ?.takeIf { it.startsWith("http://") || it.startsWith("https://") }
            ?: buildFaviconUrl(url)
    }

    AsyncImage(
        model = candidateUrl,
        contentDescription = null,
        modifier = Modifier.size(20.dp),
        error = rememberVectorPainter(image = Icons.Default.Language),
        fallback = rememberVectorPainter(image = Icons.Default.Language),
        placeholder = rememberVectorPainter(image = Icons.Default.Language)
    )
}

private fun flattenBookmarkTree(
    items: List<BookmarkItem>,
    expandedFolders: Map<String, Boolean>,
    depth: Int = 0,
    parentKey: String = "root"
): List<VisibleBookmarkNode> {
    val flattened = mutableListOf<VisibleBookmarkNode>()
    items.forEachIndexed { index, item ->
        val nodeKey = "$parentKey/$index"
        flattened.add(
            VisibleBookmarkNode(
                key = nodeKey,
                depth = depth,
                item = item
            )
        )
        if (item is BookmarkItem.Folder && expandedFolders[nodeKey] == true) {
            flattened += flattenBookmarkTree(
                items = item.children,
                expandedFolders = expandedFolders,
                depth = depth + 1,
                parentKey = nodeKey
            )
        }
    }
    return flattened
}

private fun buildFaviconUrl(url: String): String? {
    val host = runCatching { url.toUri().host }.getOrNull() ?: return null
    if (host.isBlank()) return null
    return "https://www.google.com/s2/favicons?sz=64&domain=$host"
}

private data class VisibleBookmarkNode(
    val key: String,
    val depth: Int,
    val item: BookmarkItem
)

private val ExpandedFoldersSaver = Saver<SnapshotStateMap<String, Boolean>, ArrayList<String>>(
    save = { state ->
        ArrayList<String>(state.size).apply {
            state.forEach { (key, isExpanded) ->
                if (isExpanded) add(key)
            }
        }
    },
    restore = { saved ->
        mutableStateMapOf<String, Boolean>().apply {
            saved.forEach { key ->
                this[key] = true
            }
        }
    }
)
