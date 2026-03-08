package com.hdw.bookmarker.feature.home.bookmarkdisplay

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.home.contract.BookmarkDisplayType

@Composable
fun BookmarkDisplayContent(
    bookmarkDocument: BookmarkDocument,
    onBookmarkClick: (String) -> Unit,
    onItemLongClick: (BookmarkItem, List<Int>) -> Unit,
    displayType: BookmarkDisplayType,
    folderIconStyle: BookmarkFolderIconStyle,
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

    Column(modifier = modifier.fillMaxSize()) {
        if (!snapshotTitle.isNullOrBlank()) {
            SnapshotTitleText(
                snapshotTitle = snapshotTitle,
                onSnapshotTitleClick = onSnapshotTitleClick,
            )
        }

        when (displayType) {
            BookmarkDisplayType.LIST -> {
                BookmarkListContent(
                    bookmarkDocument = bookmarkDocument,
                    onBookmarkClick = onBookmarkClick,
                    onItemLongClick = onItemLongClick,
                    onSelectedFolderPathChange = onSelectedFolderPathChange,
                    folderIconStyle = folderIconStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            }

            BookmarkDisplayType.ICON -> {
                BookmarkIconContent(
                    bookmarkDocument = bookmarkDocument,
                    onBookmarkClick = onBookmarkClick,
                    onItemLongClick = onItemLongClick,
                    folderIconStyle = folderIconStyle,
                    onSelectedFolderPathChange = onSelectedFolderPathChange,
                    selectedFolderPath = selectedFolderPath,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            }
        }
    }
}

@Composable
private fun EmptyBookmarks(
    modifier: Modifier = Modifier,
    snapshotTitle: String? = null,
    onSnapshotTitleClick: (() -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (!snapshotTitle.isNullOrBlank()) {
            SnapshotTitleText(
                snapshotTitle = snapshotTitle,
                onSnapshotTitleClick = onSnapshotTitleClick,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = stringResource(R.string.empty_bookmarks))
        }
    }
}

@Composable
private fun SnapshotTitleText(
    snapshotTitle: String,
    onSnapshotTitleClick: (() -> Unit)?,
) {
    Text(
        text = snapshotTitle,
        style = MaterialTheme.typography.titleLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
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
        folderIconStyle = BookmarkFolderIconStyle(),
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
        folderIconStyle = BookmarkFolderIconStyle(),
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
