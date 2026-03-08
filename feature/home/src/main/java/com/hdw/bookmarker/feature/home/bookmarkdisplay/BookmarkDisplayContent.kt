package com.hdw.bookmarker.feature.home.bookmarkdisplay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    selectedFolderPath: List<Int>? = null,
    snapshotTitle: String? = null,
    onSnapshotTitleClick: (() -> Unit)? = null,
) {
    if (bookmarkDocument.rootItems.isEmpty()) {
        EmptyBookmarks(
            modifier = modifier,
            snapshotTitle = snapshotTitle,
            onSnapshotTitleClick = onSnapshotTitleClick,
        )
        return
    }

    val titleTopPadding = if (snapshotTitle.isNullOrBlank()) 0.dp else 30.dp
    Box(modifier = modifier.fillMaxSize()) {
        when (displayType) {
            BookmarkDisplayType.LIST -> {
                BookmarkListContent(
                    bookmarkDocument = bookmarkDocument,
                    onBookmarkClick = onBookmarkClick,
                    onItemLongClick = onItemLongClick,
                    onSelectedFolderPathChange = onSelectedFolderPathChange,
                    folderIconShape = folderIconShape,
                    folderIconColor = folderIconColor,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = titleTopPadding),
                )
            }

            BookmarkDisplayType.ICON -> {
                BookmarkIconContent(
                    bookmarkDocument = bookmarkDocument,
                    onBookmarkClick = onBookmarkClick,
                    onItemLongClick = onItemLongClick,
                    folderIconShape = folderIconShape,
                    folderIconColor = folderIconColor,
                    onSelectedFolderPathChange = onSelectedFolderPathChange,
                    selectedFolderPath = selectedFolderPath,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = titleTopPadding),
                )
            }
        }

        if (!snapshotTitle.isNullOrBlank()) {
            Text(
                text = snapshotTitle,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .then(
                        if (onSnapshotTitleClick != null) {
                            Modifier.clickable(onClick = onSnapshotTitleClick)
                        } else {
                            Modifier
                        },
                    ),
            )
        }
    }
}

@Composable
private fun EmptyBookmarks(
    modifier: Modifier = Modifier,
    snapshotTitle: String? = null,
    onSnapshotTitleClick: (() -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxSize()) {
        val titleTopPadding = if (snapshotTitle.isNullOrBlank()) 0.dp else 44.dp
        if (!snapshotTitle.isNullOrBlank()) {
            Text(
                text = snapshotTitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .then(
                        if (onSnapshotTitleClick != null) {
                            Modifier.clickable(onClick = onSnapshotTitleClick)
                        } else {
                            Modifier
                        },
                    ),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = titleTopPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = stringResource(R.string.empty_bookmarks))
        }
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
        snapshotTitle = "북마크1",
        onSnapshotTitleClick = {},
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
        snapshotTitle = "북마크1",
        onSnapshotTitleClick = {},
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
