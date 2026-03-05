package com.hdw.bookmarker.feature.home.bookmarkdisplay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconShape

@Composable
fun BookmarkDisplayContent(
    bookmarkDocument: BookmarkDocument,
    onBookmarkClick: (String) -> Unit,
    onItemLongClick: (BookmarkItem, List<Int>) -> Unit,
    displayType: BookmarkDisplayType,
    folderIconShape: BookmarkFolderIconShape,
    folderIconColor: BookmarkFolderIconColor,
    modifier: Modifier = Modifier,
    onSelectedFolderPathChange: (List<Int>?) -> Unit = {},
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
                onSelectedFolderPathChange = onSelectedFolderPathChange,
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

@Preview(showBackground = true)
@Composable
private fun EmptyBookmarksPreview() {
    EmptyBookmarks()
}

@Preview(showBackground = true)
@Composable
private fun BookmarkDisplayContentListPreview() {
    BookmarkDisplayContent(
        bookmarkDocument = previewBookmarkDocument(),
        onBookmarkClick = {},
        onItemLongClick = { _, _ -> },
        displayType = BookmarkDisplayType.LIST,
        folderIconShape = BookmarkFolderIconShape.FILLED,
        folderIconColor = BookmarkFolderIconColor.DEFAULT,
    )
}

@Preview(showBackground = true)
@Composable
private fun BookmarkDisplayContentIconPreview() {
    BookmarkDisplayContent(
        bookmarkDocument = previewBookmarkDocument(),
        onBookmarkClick = {},
        onItemLongClick = { _, _ -> },
        displayType = BookmarkDisplayType.ICON,
        folderIconShape = BookmarkFolderIconShape.FILLED,
        folderIconColor = BookmarkFolderIconColor.DEFAULT,
    )
}

private fun previewBookmarkDocument(): BookmarkDocument = BookmarkDocument(
    title = "Sample",
    metas = emptyMap(),
    rootItems = listOf(
        BookmarkItem.Folder(
            title = "Android",
            addDate = null,
            lastModified = null,
            children = listOf(
                BookmarkItem.Bookmark(
                    title = "Android Developers",
                    url = "https://developer.android.com",
                    addDate = null,
                    lastModified = null,
                    iconUri = null,
                ),
            ),
        ),
        BookmarkItem.Bookmark(
            title = "Kotlin",
            url = "https://kotlinlang.org",
            addDate = null,
            lastModified = null,
            iconUri = null,
        ),
    ),
)
