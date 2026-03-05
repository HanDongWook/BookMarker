package com.hdw.bookmarker.feature.home.bookmarkcontent

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.ui.BookmarkSiteImage
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.feature.home.model.VisibleBookmarkNode

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
    },
)

@Composable
internal fun BookmarkListContent(
    bookmarkDocument: BookmarkDocument,
    onBookmarkClick: (String) -> Unit,
    onItemLongClick: (BookmarkItem, List<Int>) -> Unit,
    folderIconShape: BookmarkFolderIconShape,
    folderIconColor: BookmarkFolderIconColor,
    modifier: Modifier = Modifier,
) {
    val expandedFolders = rememberSaveable(saver = ExpandedFoldersSaver) {
        mutableStateMapOf<String, Boolean>()
    }
    val visibleNodes = remember(bookmarkDocument, expandedFolders.toMap()) {
        flattenBookmarkTree(
            items = bookmarkDocument.rootItems,
            expandedFolders = expandedFolders,
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(start = 8.dp, top = 4.dp, end = 4.dp, bottom = 4.dp)
    ) {
        items(items = visibleNodes, key = { it.key }) { node ->
            when (val item = node.item) {
                is BookmarkItem.Folder -> {
                    BookmarkFolderRow(
                        folder = item,
                        depth = node.depth,
                        isExpanded = expandedFolders[node.key] == true,
                        folderIconShape = folderIconShape,
                        folderIconColor = folderIconColor,
                        onLongClick = { onItemLongClick(item, node.path) },
                        onToggle = {
                            if (expandedFolders[node.key] == true) {
                                expandedFolders.remove(node.key)
                            } else {
                                expandedFolders[node.key] = true
                            }
                        },
                    )
                }

                is BookmarkItem.Bookmark -> {
                    BookmarkLeafRow(
                        bookmark = item,
                        depth = node.depth,
                        onClick = { onBookmarkClick(item.url) },
                        onLongClick = { onItemLongClick(item, node.path) },
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
    folderIconShape: BookmarkFolderIconShape,
    folderIconColor: BookmarkFolderIconColor,
    onLongClick: () -> Unit,
    onToggle: () -> Unit,
) {
    val directChildCount = folder.children.size
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onToggle,
                onLongClick = onLongClick,
            )
            .padding(start = (depth * 16).dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = folderIconShape.iconVector(),
            contentDescription = null,
            tint = folderIconColor.resolveTint(),
        )
        Text(
            text = "${folder.title} ($directChildCount)",
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f),
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
        )
    }
}

@Composable
private fun BookmarkLeafRow(
    bookmark: BookmarkItem.Bookmark,
    depth: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(start = (depth * 16).dp + 8.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BookmarkSiteImage(
            iconUri = bookmark.iconUri,
            url = bookmark.url,
            title = bookmark.title,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = bookmark.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 10.dp),
        )
    }
}

private fun flattenBookmarkTree(
    items: List<BookmarkItem>,
    expandedFolders: Map<String, Boolean>,
    depth: Int = 0,
    pathPrefix: List<Int> = emptyList(),
): List<VisibleBookmarkNode> {
    val flattened = mutableListOf<VisibleBookmarkNode>()
    items.forEachIndexed { index, item ->
        val nodePath = pathPrefix + index
        val nodeKey = nodePath.joinToString(separator = "/")
        flattened.add(
            VisibleBookmarkNode(
                key = nodeKey,
                depth = depth,
                item = item,
                path = nodePath,
            ),
        )
        if (item is BookmarkItem.Folder && expandedFolders[nodeKey] == true) {
            flattened += flattenBookmarkTree(
                items = item.children,
                expandedFolders = expandedFolders,
                depth = depth + 1,
                pathPrefix = nodePath,
            )
        }
    }
    return flattened
}
