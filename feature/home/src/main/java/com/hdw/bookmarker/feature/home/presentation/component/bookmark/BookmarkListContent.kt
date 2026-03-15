package com.hdw.bookmarker.feature.home.presentation.component.bookmark

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.model.settings.BookmarkSecondaryDisplayType
import com.hdw.bookmarker.core.ui.BookmarkSiteImage
import com.hdw.bookmarker.core.ui.folderstyle.iconVector
import com.hdw.bookmarker.core.ui.folderstyle.resolveTint

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
    onBlankAreaLongClick: (List<Int>?) -> Unit,
    scrollLongSecondaryInfo: Boolean,
    secondaryDisplayType: BookmarkSecondaryDisplayType,
    showFolderDescription: Boolean,
    scrollLongFolderDescription: Boolean,
    folderIconStyle: BookmarkFolderIconStyle,
    modifier: Modifier = Modifier,
    selectedFolderPath: List<Int>? = null,
    onSelectedFolderPathChange: (List<Int>?) -> Unit = {},
) {
    val expandedFolders = rememberSaveable(saver = ExpandedFoldersSaver) {
        mutableStateMapOf<String, Boolean>()
    }
    var selectedFolderKey by rememberSaveable { mutableStateOf<String?>(null) }
    val visibleNodes = remember(bookmarkDocument, expandedFolders.toMap()) {
        flattenBookmarkTree(
            items = bookmarkDocument.rootItems,
            expandedFolders = expandedFolders,
        )
    }

    androidx.compose.runtime.LaunchedEffect(selectedFolderPath) {
        selectedFolderKey = selectedFolderPath?.joinToString(separator = "/")
        selectedFolderPath
            ?.runningFold(emptyList<Int>()) { acc, index -> acc + index }
            ?.drop(1)
            ?.forEach { folderPath ->
                expandedFolders[folderPath.joinToString(separator = "/")] = true
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                onClick = {},
                onLongClick = { onBlankAreaLongClick(selectedFolderPath) },
            ),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(start = 8.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        ) {
            items(items = visibleNodes, key = { it.key }) { node ->
                when (val item = node.item) {
                    is BookmarkItem.Folder -> {
                        BookmarkFolderRow(
                            folder = item,
                            depth = node.depth,
                            isExpanded = expandedFolders[node.key] == true,
                            isSelected = selectedFolderKey == node.key,
                            showFolderDescription = showFolderDescription,
                            scrollLongFolderDescription = scrollLongFolderDescription,
                            folderIconStyle = folderIconStyle,
                            onLongClick = { onItemLongClick(item, node.path) },
                            onToggle = {
                                if (selectedFolderKey == node.key) {
                                    onSelectedFolderPathChange(null)
                                    if (expandedFolders[node.key] == true) {
                                        expandedFolders.remove(node.key)
                                    } else {
                                        expandedFolders[node.key] = true
                                    }
                                } else {
                                    onSelectedFolderPathChange(node.path)
                                }
                            },
                        )
                    }

                    is BookmarkItem.Bookmark -> {
                        BookmarkLeafRow(
                            bookmark = item,
                            depth = node.depth,
                            scrollLongSecondaryInfo = scrollLongSecondaryInfo,
                            secondaryDisplayType = secondaryDisplayType,
                            onClick = { onBookmarkClick(item.url) },
                            onLongClick = { onItemLongClick(item, node.path) },
                        )
                    }
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
    isSelected: Boolean,
    showFolderDescription: Boolean,
    scrollLongFolderDescription: Boolean,
    folderIconStyle: BookmarkFolderIconStyle,
    onLongClick: () -> Unit,
    onToggle: () -> Unit,
) {
    val directChildCount = folder.children.size
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (depth * 16).dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) {
                    folderIconStyle.color.resolveTint().copy(alpha = 0.25f)
                } else {
                    Color.Transparent
                },
            )
            .combinedClickable(
                onClick = onToggle,
                onLongClick = onLongClick,
            )
            .padding(start = 8.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = folderIconStyle.shape.iconVector(),
            contentDescription = null,
            tint = folderIconStyle.color.resolveTint(),
        )
        if (showFolderDescription && !folder.description.isNullOrBlank()) {
            Row(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${folder.title} ($directChildCount)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                RightAlignedSecondaryText(
                    text = folder.description.orEmpty(),
                    scrollLongText = scrollLongFolderDescription,
                )
            }
        } else {
            Text(
                text = "${folder.title} ($directChildCount)",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f),
            )
        }
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BookmarkLeafRow(
    bookmark: BookmarkItem.Bookmark,
    depth: Int,
    scrollLongSecondaryInfo: Boolean,
    secondaryDisplayType: BookmarkSecondaryDisplayType,
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
        Row(
            modifier = Modifier
                .padding(start = 10.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = bookmark.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            val secondaryText = when (secondaryDisplayType) {
                BookmarkSecondaryDisplayType.NONE -> null

                BookmarkSecondaryDisplayType.URL -> bookmark.url

                BookmarkSecondaryDisplayType.TAG -> if (bookmark.tags.isNotEmpty()) {
                    bookmark.tags.joinToString(separator = " ") { "#$it" }
                } else {
                    null
                }
            }
            if (secondaryText != null) {
                RightAlignedSecondaryText(
                    text = secondaryText,
                    scrollLongText = scrollLongSecondaryInfo,
                )
            }
        }
    }
}

@Composable
private fun RowScope.RightAlignedSecondaryText(text: String, scrollLongText: Boolean) {
    Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = if (scrollLongText) TextOverflow.Visible else TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (scrollLongText) {
                        Modifier.basicMarquee()
                    } else {
                        Modifier
                    },
                ),
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

@Preview(showBackground = true)
@Composable
internal fun BookmarkListContentPreview() {
    BookmarkListContent(
        bookmarkDocument = previewBookmarkListDocument(),
        onBookmarkClick = {},
        onItemLongClick = { _, _ -> },
        onBlankAreaLongClick = {},
        scrollLongSecondaryInfo = true,
        secondaryDisplayType = BookmarkSecondaryDisplayType.URL,
        showFolderDescription = true,
        scrollLongFolderDescription = true,
        folderIconStyle = BookmarkFolderIconStyle(),
        selectedFolderPath = listOf(0),
    )
}

@Preview(showBackground = true)
@Composable
internal fun BookmarkFolderRowPreview() {
    BookmarkFolderRow(
        folder = BookmarkItem.Folder(
            title = "Development",
            description = "Development resources",
            addDate = null,
            lastModified = null,
            children = listOf(
                BookmarkItem.Bookmark(
                    title = "Android",
                    url = "https://developer.android.com",
                    addDate = null,
                    lastModified = null,
                    iconUri = null,
                ),
            ),
        ),
        depth = 0,
        isExpanded = true,
        isSelected = true,
        showFolderDescription = true,
        scrollLongFolderDescription = true,
        folderIconStyle = BookmarkFolderIconStyle(),
        onLongClick = {},
        onToggle = {},
    )
}

@Preview(showBackground = true)
@Composable
internal fun BookmarkLeafRowPreview() {
    BookmarkLeafRow(
        bookmark = BookmarkItem.Bookmark(
            title = "Kotlin",
            url = "https://kotlinlang.org",
            addDate = null,
            lastModified = null,
            iconUri = null,
        ),
        depth = 0,
        scrollLongSecondaryInfo = true,
        secondaryDisplayType = BookmarkSecondaryDisplayType.URL,
        onClick = {},
        onLongClick = {},
    )
}

private fun previewBookmarkListDocument(): BookmarkDocument = BookmarkDocument(
    title = "Sample",
    metas = emptyMap(),
    rootItems = listOf(
        BookmarkItem.Folder(
            title = "Engineering",
            description = "Engineering references",
            addDate = null,
            lastModified = null,
            children = listOf(
                BookmarkItem.Bookmark(
                    title = "Kotlin",
                    url = "https://kotlinlang.org",
                    addDate = null,
                    lastModified = null,
                    iconUri = null,
                ),
            ),
        ),
        BookmarkItem.Bookmark(
            title = "Compose",
            url = "https://developer.android.com/jetpack/compose",
            addDate = null,
            lastModified = null,
            iconUri = null,
            tags = listOf("jetpack", "compose"),
        ),
    ),
)

private data class VisibleBookmarkNode(val key: String, val depth: Int, val item: BookmarkItem, val path: List<Int>)
