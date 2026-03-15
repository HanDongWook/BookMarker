package com.hdw.bookmarker.feature.home.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.ui.BookmarkSiteImage
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.iconVector
import com.hdw.bookmarker.core.ui.folderstyle.resolveTint
import com.hdw.bookmarker.feature.home.search.model.BookmarkSearchItemType
import com.hdw.bookmarker.feature.home.search.model.BookmarkSearchResult

@Composable
internal fun BookmarkSearchResultList(
    results: List<BookmarkSearchResult>,
    folderIconStyle: BookmarkFolderIconStyle,
    onResultClick: (BookmarkSearchResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    ) {
        itemsIndexed(
            items = results,
            key = { _, result ->
                "${result.snapshotId.value}:${result.itemType}:${result.itemPath.joinToString(separator = "/")}"
            },
        ) { index, result ->
            BookmarkSearchResultRow(
                result = result,
                folderIconStyle = folderIconStyle,
                onClick = { onResultClick(result) },
            )
            if (index < results.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                )
            }
        }
    }
}

@Composable
private fun BookmarkSearchResultRow(
    result: BookmarkSearchResult,
    folderIconStyle: BookmarkFolderIconStyle,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        when (result.itemType) {
            BookmarkSearchItemType.FOLDER -> {
                Icon(
                    imageVector = folderIconStyle.shape.iconVector(),
                    contentDescription = null,
                    tint = folderIconStyle.color.resolveTint(),
                    modifier = Modifier.size(24.dp),
                )
            }

            BookmarkSearchItemType.BOOKMARK -> {
                BookmarkSiteImage(
                    iconUri = result.bookmarkIconUri,
                    url = result.bookmarkUrl.orEmpty(),
                    title = result.title,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = result.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            result.secondaryText
                ?.takeIf(String::isNotBlank)
                ?.let { secondaryText ->
                    Text(
                        text = secondaryText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            if (result.tags.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.bookmark_search_tags_prefix) + result.tags.joinToString(separator = " ") { "#$it" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = stringResource(R.string.bookmark_search_location_prefix) + result.breadcrumb,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkSearchResultListPreview() {
    BookmarkSearchResultList(
        results = listOf(
            BookmarkSearchResult(
                snapshotId = SnapshotId("snapshot-1"),
                snapshotTitle = "Bookmark 1",
                itemPath = listOf(0),
                revealFolderPath = listOf(0),
                itemType = BookmarkSearchItemType.FOLDER,
                title = "Engineering",
                secondaryText = "Engineering references",
                breadcrumb = "Bookmark 1",
            ),
            BookmarkSearchResult(
                snapshotId = SnapshotId("snapshot-1"),
                snapshotTitle = "Bookmark 1",
                itemPath = listOf(0, 0),
                revealFolderPath = listOf(0),
                itemType = BookmarkSearchItemType.BOOKMARK,
                title = "Android Developers",
                secondaryText = "https://developer.android.com/jetpack/compose",
                breadcrumb = "Bookmark 1 / Engineering",
                bookmarkUrl = "https://developer.android.com/jetpack/compose",
                bookmarkIconUri = null,
                tags = listOf("jetpack", "compose"),
            ),
        ),
        folderIconStyle = BookmarkFolderIconStyle(),
        onResultClick = {},
    )
}
