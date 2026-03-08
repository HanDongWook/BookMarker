package com.hdw.bookmarker.feature.home.bookmarkdisplay

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.ui.BookmarkSiteImage
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconShape

@Composable
internal fun BookmarkIconContent(
    bookmarkDocument: BookmarkDocument,
    onBookmarkClick: (String) -> Unit,
    onItemLongClick: (BookmarkItem, List<Int>) -> Unit,
    folderIconShape: BookmarkFolderIconShape,
    folderIconColor: BookmarkFolderIconColor,
    modifier: Modifier = Modifier,
    selectedFolderPath: List<Int>? = null,
    onSelectedFolderPathChange: (List<Int>?) -> Unit = {},
) {
    val currentPath = selectedFolderPath.orEmpty()
    val traversedFolders = mutableListOf<BookmarkItem.Folder>()
    var currentItems = bookmarkDocument.rootItems
    var isPathValid = true
    for (index in currentPath) {
        val folder = currentItems.getOrNull(index) as? BookmarkItem.Folder
        if (folder == null) {
            isPathValid = false
            break
        }
        traversedFolders += folder
        currentItems = folder.children
    }
    val currentFolderPath = traversedFolders.joinToString(separator = "/") { it.title }

    LaunchedEffect(isPathValid, selectedFolderPath) {
        if (!isPathValid && selectedFolderPath != null) {
            onSelectedFolderPathChange(null)
        }
    }

    BackHandler(enabled = currentPath.isNotEmpty()) {
        onSelectedFolderPathChange(currentPath.dropLast(1).takeIf { it.isNotEmpty() })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp),
    ) {
        if (currentPath.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        onSelectedFolderPathChange(currentPath.dropLast(1).takeIf { it.isNotEmpty() })
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.bookmark_icon_back),
                    )
                }
                Text(
                    text = currentFolderPath,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis,
                    modifier = Modifier.padding(end = 12.dp),
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 96.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 16.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(
                items = currentItems,
                key = { index, item ->
                    when (item) {
                        is BookmarkItem.Bookmark -> "bookmark-${currentPath.size}-$index-${item.url}"
                        is BookmarkItem.Folder -> "folder-${currentPath.size}-$index-${item.title}"
                    }
                },
            ) { index, item ->
                when (item) {
                    is BookmarkItem.Folder -> {
                        BookmarkFolderIconItem(
                            folder = item,
                            folderIconShape = folderIconShape,
                            folderIconColor = folderIconColor,
                            onClick = {
                                onSelectedFolderPathChange(currentPath + index)
                            },
                            onLongClick = {
                                onItemLongClick(item, currentPath + index)
                            },
                        )
                    }

                    is BookmarkItem.Bookmark -> {
                        BookmarkLeafIconItem(
                            bookmark = item,
                            onClick = { onBookmarkClick(item.url) },
                            onLongClick = {
                                onItemLongClick(item, currentPath + index)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkFolderIconItem(
    folder: BookmarkItem.Folder,
    folderIconShape: BookmarkFolderIconShape,
    folderIconColor: BookmarkFolderIconColor,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = folderIconShape.iconVector(),
            contentDescription = null,
            tint = folderIconColor.resolveTint(),
            modifier = Modifier.size(36.dp),
        )
        Text(
            text = folder.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp),
        )
        Text(
            text = "${folder.children.size}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BookmarkLeafIconItem(bookmark: BookmarkItem.Bookmark, onClick: () -> Unit, onLongClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BookmarkSiteImage(
            iconUri = bookmark.iconUri,
            url = bookmark.url,
            title = bookmark.title,
            modifier = Modifier.size(36.dp),
        )
        Text(
            text = bookmark.title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkIconContentPreview() {
    BookmarkIconContent(
        bookmarkDocument = previewBookmarkIconDocument(),
        onBookmarkClick = {},
        onItemLongClick = { _, _ -> },
        folderIconShape = BookmarkFolderIconShape.FILLED,
        folderIconColor = BookmarkFolderIconColor.DEFAULT,
    )
}

@Preview(showBackground = true)
@Composable
private fun BookmarkFolderIconItemPreview() {
    BookmarkFolderIconItem(
        folder = BookmarkItem.Folder(
            title = "Favorites",
            addDate = null,
            lastModified = null,
            children = emptyList(),
        ),
        folderIconShape = BookmarkFolderIconShape.FILLED,
        folderIconColor = BookmarkFolderIconColor.DEFAULT,
        onClick = {},
        onLongClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BookmarkLeafIconItemPreview() {
    BookmarkLeafIconItem(
        bookmark = BookmarkItem.Bookmark(
            title = "OpenAI",
            url = "https://openai.com",
            addDate = null,
            lastModified = null,
            iconUri = null,
        ),
        onClick = {},
        onLongClick = {},
    )
}

private fun previewBookmarkIconDocument(): BookmarkDocument = BookmarkDocument(
    title = "Sample",
    metas = emptyMap(),
    rootItems = listOf(
        BookmarkItem.Folder(
            title = "AI",
            addDate = null,
            lastModified = null,
            children = listOf(
                BookmarkItem.Bookmark(
                    title = "OpenAI",
                    url = "https://openai.com",
                    addDate = null,
                    lastModified = null,
                    iconUri = null,
                ),
            ),
        ),
        BookmarkItem.Bookmark(
            title = "Android",
            url = "https://developer.android.com",
            addDate = null,
            lastModified = null,
            iconUri = null,
        ),
    ),
)
