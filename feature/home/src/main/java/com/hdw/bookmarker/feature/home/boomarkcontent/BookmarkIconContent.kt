package com.hdw.bookmarker.feature.home.boomarkcontent

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.ui.BookmarkSiteImage
import com.hdw.bookmarker.feature.home.R

@Composable
internal fun BookmarkIconContent(
    bookmarkDocument: BookmarkDocument,
    onBookmarkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val folderDepthStack = remember(bookmarkDocument) { mutableStateListOf<BookmarkItem.Folder>() }
    val currentFolder = folderDepthStack.lastOrNull()
    val currentItems = currentFolder?.children ?: bookmarkDocument.rootItems

    BackHandler(enabled = folderDepthStack.isNotEmpty()) {
        folderDepthStack.removeAt(folderDepthStack.lastIndex)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp),
    ) {
        if (folderDepthStack.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        folderDepthStack.removeAt(folderDepthStack.lastIndex)
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.bookmark_icon_back),
                    )
                }
                Text(
                    text = currentFolder?.title.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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
                        is BookmarkItem.Bookmark -> "bookmark-${folderDepthStack.size}-$index-${item.url}"
                        is BookmarkItem.Folder -> "folder-${folderDepthStack.size}-$index-${item.title}"
                    }
                },
            ) { _, item ->
                when (item) {
                    is BookmarkItem.Folder -> {
                        BookmarkFolderIconItem(
                            folder = item,
                            onClick = { folderDepthStack.add(item) },
                        )
                    }

                    is BookmarkItem.Bookmark -> {
                        BookmarkLeafIconItem(
                            bookmark = item,
                            onClick = { onBookmarkClick(item.url) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkFolderIconItem(folder: BookmarkItem.Folder, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
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
private fun BookmarkLeafIconItem(bookmark: BookmarkItem.Bookmark, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
