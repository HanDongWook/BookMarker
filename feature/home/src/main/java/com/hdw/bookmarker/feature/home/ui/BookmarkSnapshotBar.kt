package com.hdw.bookmarker.feature.home.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.model.bookmark.SnapshotId

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BookmarkSnapshotBar(
    orderedSnapshotIds: List<SnapshotId>,
    inboxSnapshotIds: Set<SnapshotId>,
    bookmarkColors: Map<SnapshotId, Long>,
    selectedBookmarkId: SnapshotId?,
    isEditMode: Boolean,
    onAddClick: () -> Unit,
    onSnapshotClick: (SnapshotId) -> Unit,
    onEnterEditMode: () -> Unit,
    onDeleteRequest: (SnapshotId) -> Unit,
) {
    val shakeRotation = rememberInfiniteTransition(label = "connected_browser_shake").animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 120),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "connected_browser_shake_rotation",
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp,
            bottom = 8.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "add_bookmark") {
            Surface(
                color = Color.Transparent,
                shape = MaterialTheme.shapes.small,
            ) {
                Box(
                    modifier = Modifier
                        .combinedClickable(
                            onClick = onAddClick,
                            onLongClick = {},
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(36.dp),
                    )
                }
            }
        }

        items(orderedSnapshotIds, key = { it.value }) { snapshotId ->
            val isSelected = selectedBookmarkId == snapshotId
            val isInboxSnapshot = snapshotId in inboxSnapshotIds
            val colorValue = bookmarkColors[snapshotId] ?: 0L
            Surface(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.Transparent
                },
                shape = MaterialTheme.shapes.small,
            ) {
                Box(
                    modifier = Modifier
                        .combinedClickable(
                            onClick = { onSnapshotClick(snapshotId) },
                            onLongClick = {
                                onSnapshotClick(snapshotId)
                                onEnterEditMode()
                            },
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Icon(
                        imageVector = if (isInboxSnapshot) {
                            Icons.Default.MoveToInbox
                        } else {
                            Icons.Default.Bookmark
                        },
                        contentDescription = null,
                        tint = Color(colorValue),
                        modifier = Modifier
                            .size(36.dp)
                            .graphicsLayer {
                                rotationZ =
                                    if (isEditMode && isSelected) shakeRotation.value else 0f
                            }
                            .alpha(1f),
                    )

                    if (isEditMode) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(16.dp),
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.error,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .combinedClickable(
                                        onClick = { onDeleteRequest(snapshotId) },
                                        onLongClick = {},
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onError,
                                    modifier = Modifier.size(10.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
private fun BookmarkSnapshotBarPreview() {
    val snapshotIds = listOf(
        SnapshotId("1"),
        SnapshotId("2"),
        SnapshotId("3"),
    )
    BookmarkSnapshotBar(
        orderedSnapshotIds = snapshotIds,
        inboxSnapshotIds = setOf(SnapshotId("1")),
        bookmarkColors = mapOf(
            SnapshotId("1") to 0xFFFF0000,
            SnapshotId("2") to 0xFF00FF00,
            SnapshotId("3") to 0xFF0000FF,
        ),
        selectedBookmarkId = SnapshotId("2"),
        isEditMode = false,
        onAddClick = {},
        onSnapshotClick = {},
        onEnterEditMode = {},
        onDeleteRequest = {},
    )
}
