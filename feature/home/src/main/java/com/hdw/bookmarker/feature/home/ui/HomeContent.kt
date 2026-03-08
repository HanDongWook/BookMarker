package com.hdw.bookmarker.feature.home.ui
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerYellow
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.home.contract.BookmarkDisplayType
import com.hdw.bookmarker.feature.home.contract.HomeState
import com.hdw.bookmarker.feature.home.ui.appbar.HomeTopAppBar
import com.hdw.bookmarker.feature.home.ui.bookmarkdisplay.BookmarkDisplayContent
import kotlinx.coroutines.launch

@Composable
internal fun HomeContent(
    state: HomeState,
    orderedSnapshotIds: List<String>,
    selectedBookmarkId: String?,
    pagerState: PagerState,
    isBrowserEditMode: Boolean,
    defaultBrowserIcon: Drawable?,
    onSettingsClick: () -> Unit,
    onBookmarkDisplayTypeToggle: () -> Unit,
    onDefaultBrowserPickerOpen: () -> Unit,
    onEditLabelClick: () -> Unit,
    onEditModeDoneClick: () -> Unit,
    onAddItemClick: () -> Unit,
    onImportClick: () -> Unit,
    onEnterEditMode: () -> Unit,
    onDeleteSnapshotRequest: (String) -> Unit,
    onBookmarkClick: (String) -> Unit,
    onItemLongClick: (BookmarkItem, List<Int>) -> Unit,
    onSelectedFolderPathChange: (String, List<Int>?) -> Unit,
    currentSnapshotTitle: String?,
    onSnapshotTitleClick: () -> Unit,
    onSnapshotShareClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeTopAppBar(
                isEditMode = isBrowserEditMode,
                bookmarkDisplayType = state.bookmarkDisplayType,
                defaultBrowserIcon = defaultBrowserIcon,
                onBookmarkDisplayTypeClick = onBookmarkDisplayTypeToggle,
                onDefaultBrowserIconClick = onDefaultBrowserPickerOpen,
                onSettingsClick = onSettingsClick,
                onEditLabelClick = onEditLabelClick,
                onEditModeDoneClick = onEditModeDoneClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddItemClick,
                shape = CircleShape,
                containerColor = BookMarkerYellow,
                contentColor = Color.Black,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_bookmark_or_folder),
                )
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            BookmarkSnapshotTabBar(
                orderedSnapshotIds = orderedSnapshotIds,
                bookmarkColors = state.bookmarkColors,
                selectedBookmarkId = selectedBookmarkId,
                isEditMode = isBrowserEditMode,
                onAddClick = onImportClick,
                onSnapshotClick = { snapshotId ->
                    val targetPage = orderedSnapshotIds.indexOf(snapshotId)
                    if (targetPage >= 0 && targetPage != pagerState.currentPage) {
                        scope.launch {
                            pagerState.animateScrollToPage(targetPage)
                        }
                    }
                },
                onEnterEditMode = onEnterEditMode,
                onDeleteRequest = onDeleteSnapshotRequest,
            )

            if (orderedSnapshotIds.isEmpty()) {
                NoBookmarkItem(
                    modifier = Modifier.weight(1f),
                    onImportClick = onImportClick,
                )
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                ) { page ->
                    val snapshotId = orderedSnapshotIds[page]
                    val bookmarkDocument = state.bookmarkDocuments[snapshotId]
                    val snapshotFolderPath = state.selectedFolderPaths.pathOf(snapshotId)
                    if (bookmarkDocument != null) {
                        BookmarkDisplayContent(
                            modifier = Modifier.fillMaxSize(),
                            bookmarkDocument = bookmarkDocument,
                            displayType = state.bookmarkDisplayType,
                            folderIconStyle = state.folderIconStyle,
                            onBookmarkClick = onBookmarkClick,
                            onItemLongClick = onItemLongClick,
                            onSelectedFolderPathChange = { path -> onSelectedFolderPathChange(snapshotId, path) },
                            selectedFolderPath = snapshotFolderPath,
                            snapshotTitle = currentSnapshotTitle,
                            onSnapshotTitleClick = onSnapshotTitleClick,
                            onSnapshotShareClick = onSnapshotShareClick,
                        )
                    } else {
                        NoBookmarkItem(
                            modifier = Modifier.fillMaxSize(),
                            onImportClick = onImportClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoBookmarkItem(modifier: Modifier, onImportClick: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.no_browsers_connected),
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onImportClick,
            modifier = Modifier.padding(top = 12.dp),
        ) {
            Text(text = stringResource(R.string.import_bookmarks))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoBookmarkItemPreview() {
    NoBookmarkItem(
        modifier = Modifier.fillMaxSize(),
        onImportClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    HomeContent(
        state = HomeState(
            orderedSnapshotIds = emptyList(),
            bookmarkDisplayType = BookmarkDisplayType.LIST,
        ),
        orderedSnapshotIds = emptyList(),
        selectedBookmarkId = null,
        pagerState = androidx.compose.foundation.pager.rememberPagerState(
            initialPage = 0,
            pageCount = { 0 },
        ),
        isBrowserEditMode = false,
        defaultBrowserIcon = null,
        onSettingsClick = {},
        onBookmarkDisplayTypeToggle = {},
        onDefaultBrowserPickerOpen = {},
        onEditLabelClick = {},
        onEditModeDoneClick = {},
        onAddItemClick = {},
        onImportClick = {},
        onEnterEditMode = {},
        onDeleteSnapshotRequest = {},
        onBookmarkClick = {},
        onItemLongClick = { _, _ -> },
        onSelectedFolderPathChange = { _, _ -> },
        currentSnapshotTitle = "북마크1",
        onSnapshotTitleClick = {},
        onSnapshotShareClick = {},
    )
}
