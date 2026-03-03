package com.hdw.bookmarker.feature.home.bookmarkcontent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconShape

@Composable
fun BookmarkContent(
    bookmarkDocument: BookmarkDocument,
    onBookmarkClick: (String) -> Unit,
    onItemLongClick: (BookmarkItem, List<Int>) -> Unit,
    displayType: BookmarkDisplayType,
    folderIconShape: BookmarkFolderIconShape,
    folderIconColor: BookmarkFolderIconColor,
    modifier: Modifier = Modifier,
) {
    if (bookmarkDocument.rootItems.isEmpty()) {
        EmptyBookmarks(modifier = modifier)
        return
    }

    when (displayType) {
        BookmarkDisplayType.LIST -> {
            BookmarkListContent(
                bookmarkDocument = bookmarkDocument,
                onBookmarkClick = onBookmarkClick,
                onItemLongClick = onItemLongClick,
                folderIconShape = folderIconShape,
                folderIconColor = folderIconColor,
                modifier = modifier,
            )
        }

        BookmarkDisplayType.ICON -> {
            BookmarkIconContent(
                bookmarkDocument = bookmarkDocument,
                onBookmarkClick = onBookmarkClick,
                onItemLongClick = onItemLongClick,
                folderIconShape = folderIconShape,
                folderIconColor = folderIconColor,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun EmptyBookmarks(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(R.string.empty_bookmarks))
    }
}
