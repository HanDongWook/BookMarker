package com.hdw.bookmarker.feature.home.presentation.component.bookmark
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.model.settings.BookmarkSecondaryDisplayType
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.home.presentation.model.BookmarkDisplayType

@Composable
fun BookmarkDisplayContent(
    bookmarkDocument: BookmarkDocument,
    isInboxSnapshot: Boolean,
    onBookmarkClick: (String) -> Unit,
    onItemLongClick: (BookmarkItem, List<Int>) -> Unit,
    onBlankAreaLongClick: (List<Int>?) -> Unit,
    displayType: BookmarkDisplayType,
    scrollLongSecondaryInfo: Boolean,
    secondaryDisplayType: BookmarkSecondaryDisplayType,
    showFolderDescription: Boolean,
    scrollLongFolderDescription: Boolean,
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
            isInboxSnapshot = isInboxSnapshot,
            snapshotTitle = snapshotTitle,
            onSnapshotTitleClick = onSnapshotTitleClick,
            onBlankAreaLongClick = onBlankAreaLongClick,
            selectedFolderPath = selectedFolderPath,
        )
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (!snapshotTitle.isNullOrBlank()) {
            SnapshotTitleBar(
                snapshotTitle = snapshotTitle,
                isInboxSnapshot = isInboxSnapshot,
                onSnapshotTitleClick = onSnapshotTitleClick,
            )
        }

        when (displayType) {
            BookmarkDisplayType.LIST -> {
                BookmarkListContent(
                    bookmarkDocument = bookmarkDocument,
                    onBookmarkClick = onBookmarkClick,
                    onItemLongClick = onItemLongClick,
                    scrollLongSecondaryInfo = scrollLongSecondaryInfo,
                    secondaryDisplayType = secondaryDisplayType,
                    showFolderDescription = showFolderDescription,
                    scrollLongFolderDescription = scrollLongFolderDescription,
                    selectedFolderPath = selectedFolderPath,
                    onSelectedFolderPathChange = onSelectedFolderPathChange,
                    folderIconStyle = folderIconStyle,
                    onBlankAreaLongClick = onBlankAreaLongClick,
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
                    onBlankAreaLongClick = onBlankAreaLongClick,
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
    isInboxSnapshot: Boolean = false,
    snapshotTitle: String? = null,
    onSnapshotTitleClick: (() -> Unit)? = null,
    onBlankAreaLongClick: (List<Int>?) -> Unit = {},
    selectedFolderPath: List<Int>? = null,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (!snapshotTitle.isNullOrBlank()) {
            SnapshotTitleBar(
                snapshotTitle = snapshotTitle,
                isInboxSnapshot = isInboxSnapshot,
                onSnapshotTitleClick = onSnapshotTitleClick,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { onBlankAreaLongClick(selectedFolderPath) },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.empty_bookmarks),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SnapshotTitleBar(
    snapshotTitle: String,
    isInboxSnapshot: Boolean = false,
    onSnapshotTitleClick: (() -> Unit)?,
) {
    Text(
        text = snapshotTitle,
        style = MaterialTheme.typography.titleLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 12.dp, top = 8.dp, bottom = 4.dp)
            .then(
                if (onSnapshotTitleClick != null && !isInboxSnapshot) {
                    Modifier.clickable(onClick = onSnapshotTitleClick)
                } else {
                    Modifier
                },
            ),
    )
}

@Preview(showBackground = true)
@Composable
internal fun EmptyBookmarksPreview() {
    EmptyBookmarks()
}

@Preview(showBackground = true)
@Composable
internal fun BookmarkDisplayContentListPreview() {
    BookmarkDisplayContent(
        bookmarkDocument = previewBookmarkDocument(),
        isInboxSnapshot = false,
        onBookmarkClick = {},
        onItemLongClick = { _, _ -> },
        onBlankAreaLongClick = {},
        displayType = BookmarkDisplayType.LIST,
        scrollLongSecondaryInfo = true,
        secondaryDisplayType = BookmarkSecondaryDisplayType.URL,
        showFolderDescription = true,
        scrollLongFolderDescription = true,
        folderIconStyle = BookmarkFolderIconStyle(),
        snapshotTitle = "북마크1",
        onSnapshotTitleClick = {},
    )
}

@Preview(showBackground = true)
@Composable
internal fun BookmarkDisplayContentIconPreview() {
    BookmarkDisplayContent(
        bookmarkDocument = previewBookmarkDocument(),
        isInboxSnapshot = false,
        onBookmarkClick = {},
        onItemLongClick = { _, _ -> },
        onBlankAreaLongClick = {},
        displayType = BookmarkDisplayType.ICON,
        scrollLongSecondaryInfo = true,
        secondaryDisplayType = BookmarkSecondaryDisplayType.URL,
        showFolderDescription = true,
        scrollLongFolderDescription = true,
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
            description = "Android development links",
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
