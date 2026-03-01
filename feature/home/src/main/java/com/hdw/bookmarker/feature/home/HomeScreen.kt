package com.hdw.bookmarker.feature.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hdw.bookmarker.core.domain.util.BookmarkColorGenerator
import com.hdw.bookmarker.core.model.MimeTypes
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.home.appbar.HomeTopAppBar
import com.hdw.bookmarker.feature.home.boomarkcontent.BookmarkContent
import com.hdw.bookmarker.feature.home.dialog.AddBookmarkDialog
import com.hdw.bookmarker.feature.home.dialog.AddFolderDialog
import com.hdw.bookmarker.feature.home.dialog.AddItemTypeDialog
import com.hdw.bookmarker.feature.home.dialog.BookmarkColorPickerDialog
import com.hdw.bookmarker.feature.home.dialog.DefaultBrowserPickerDialog
import com.hdw.bookmarker.feature.home.dialog.DeleteBookmarkItemDialog
import com.hdw.bookmarker.feature.home.dialog.DeleteBookmarkSnapshotDialog
import com.hdw.bookmarker.feature.home.dialog.ImportOptionDialog
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeRoute(
    onSettingsClick: () -> Unit,
    onOpenBookmark: (String, String?) -> Boolean,
    onOpenBookmarkImportGuide: () -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val resources = LocalResources.current
    val htmlPickerLauncher = rememberLauncherForActivityResult(OpenDocument()) { uri ->
        if (uri != null) {
            viewModel.onHtmlFileSelected(uri)
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is HomeSideEffect.ShowError -> {
                val message = resources.getString(sideEffect.messageResId)
                val toastText = if (sideEffect.detail.isNullOrBlank()) {
                    message
                } else {
                    resources.getString(R.string.error_with_detail, message, sideEffect.detail)
                }
                context.showShortToast(toastText)
            }

            is HomeSideEffect.ShowMessage -> {
                context.showShortToast(resources.getString(sideEffect.messageResId))
            }

            is HomeSideEffect.OpenFilePicker -> {
                htmlPickerLauncher.launch(arrayOf(MimeTypes.HTML))
            }
        }
    }

    HomeScreen(
        state = state,
        onSettingsClick = onSettingsClick,
        onOpenBookmark = onOpenBookmark,
        onOpenBookmarkImportGuide = onOpenBookmarkImportGuide,
        onSnapshotSelected = viewModel::onSnapshotSelected,
        onBookmarkColorSelected = viewModel::onBookmarkColorSelected,
        onDefaultBrowserSelected = viewModel::onDefaultBrowserSelected,
        onOpenFilePicker = viewModel::openFilePicker,
        onDeleteBookmarkSnapshot = viewModel::deleteBookmarkSnapshot,
        onBookmarkDisplayTypeToggle = viewModel::onBookmarkDisplayTypeToggle,
        onAddFolder = viewModel::addFolder,
        onAddBookmark = viewModel::addBookmark,
        onDeleteBookmarkItem = viewModel::deleteBookmarkItem,
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    onSettingsClick: () -> Unit,
    onOpenBookmark: (String, String?) -> Boolean,
    onOpenBookmarkImportGuide: () -> Unit,
    onSnapshotSelected: (String) -> Unit,
    onBookmarkColorSelected: (String, Long) -> Unit,
    onDefaultBrowserSelected: (String) -> Unit,
    onOpenFilePicker: () -> Unit,
    onDeleteBookmarkSnapshot: (String) -> Unit,
    onBookmarkDisplayTypeToggle: () -> Unit,
    onAddFolder: (String) -> Unit,
    onAddBookmark: (String, String) -> Unit,
    onDeleteBookmarkItem: (List<Int>) -> Unit,
) {
    val orderedSnapshotIds = state.orderedSnapshotIds
    val context = LocalContext.current
    val resources = LocalResources.current
    var showImportOptionDialog by rememberSaveable { mutableStateOf(false) }
    var isBrowserEditMode by rememberSaveable { mutableStateOf(false) }
    var showDefaultBrowserDialog by rememberSaveable { mutableStateOf(false) }
    var showColorPickerDialog by rememberSaveable { mutableStateOf(false) }
    var showAddItemTypeDialog by rememberSaveable { mutableStateOf(false) }
    var showAddFolderDialog by rememberSaveable { mutableStateOf(false) }
    var showAddBookmarkDialog by rememberSaveable { mutableStateOf(false) }
    var pendingFolderTitle by rememberSaveable { mutableStateOf("") }
    var pendingBookmarkTitle by rememberSaveable { mutableStateOf("") }
    var pendingBookmarkUrl by rememberSaveable { mutableStateOf("") }
    var pendingDeleteSnapshotId by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingDeleteBookmarkItemPath by remember { mutableStateOf<List<Int>?>(null) }

    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { orderedSnapshotIds.size },
    )
    val selectedBookmarkId = state.selectedBookmarkId
        ?.takeIf { id -> orderedSnapshotIds.contains(id) }
        ?: orderedSnapshotIds.getOrNull(pagerState.currentPage)
        ?: orderedSnapshotIds.firstOrNull()

    val defaultBrowserIcon = state.installedBrowsers
        .firstOrNull { it.packageName == state.defaultBrowserPackage }
        ?.icon

    LaunchedEffect(pagerState, orderedSnapshotIds) {
        if (orderedSnapshotIds.isEmpty()) return@LaunchedEffect
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                orderedSnapshotIds.getOrNull(page)?.let(onSnapshotSelected)
            }
    }

    BackHandler(enabled = showImportOptionDialog) {
        showImportOptionDialog = false
    }

    BackHandler(enabled = isBrowserEditMode) {
        isBrowserEditMode = false
    }

    BackHandler(enabled = showColorPickerDialog) {
        showColorPickerDialog = false
    }

    BackHandler(enabled = showAddItemTypeDialog) {
        showAddItemTypeDialog = false
    }

    BackHandler(enabled = showAddFolderDialog) {
        showAddFolderDialog = false
    }

    BackHandler(enabled = showAddBookmarkDialog) {
        showAddBookmarkDialog = false
    }

    BackHandler(enabled = pendingDeleteBookmarkItemPath != null) {
        pendingDeleteBookmarkItemPath = null
    }

    if (showColorPickerDialog && selectedBookmarkId != null) {
        BookmarkColorPickerDialog(
            colors = BookmarkColorGenerator.getAllColors(),
            currentColor = state.bookmarkColors[selectedBookmarkId]
                ?: BookmarkColorGenerator.generateColorForId(selectedBookmarkId),
            onColorSelect = { color ->
                onBookmarkColorSelected(selectedBookmarkId, color)
                showColorPickerDialog = false
            },
            onDismiss = { showColorPickerDialog = false },
        )
    }

    if (showDefaultBrowserDialog) {
        DefaultBrowserPickerDialog(
            installedBrowsers = state.installedBrowsers,
            selectedPackage = state.defaultBrowserPackage,
            onSelect = { packageName ->
                onDefaultBrowserSelected(packageName)
                showDefaultBrowserDialog = false
            },
            onDismiss = {
                showDefaultBrowserDialog = false
            },
        )
    }

    if (showImportOptionDialog) {
        ImportOptionDialog(
            onDismiss = { showImportOptionDialog = false },
            onOpenGuide = {
                showImportOptionDialog = false
                onOpenBookmarkImportGuide()
            },
            onPickFile = {
                showImportOptionDialog = false
                onOpenFilePicker()
            },
        )
    }

    if (pendingDeleteSnapshotId != null) {
        DeleteBookmarkSnapshotDialog(
            onDismiss = { pendingDeleteSnapshotId = null },
            onConfirmDelete = {
                onDeleteBookmarkSnapshot(pendingDeleteSnapshotId ?: return@DeleteBookmarkSnapshotDialog)
                pendingDeleteSnapshotId = null
            },
        )
    }

    if (pendingDeleteBookmarkItemPath != null) {
        DeleteBookmarkItemDialog(
            onDismiss = { pendingDeleteBookmarkItemPath = null },
            onConfirmDelete = {
                onDeleteBookmarkItem(pendingDeleteBookmarkItemPath ?: return@DeleteBookmarkItemDialog)
                pendingDeleteBookmarkItemPath = null
            },
        )
    }

    if (showAddItemTypeDialog) {
        AddItemTypeDialog(
            onDismiss = { showAddItemTypeDialog = false },
            onAddFolderClick = {
                showAddItemTypeDialog = false
                pendingFolderTitle = ""
                showAddFolderDialog = true
            },
            onAddBookmarkClick = {
                showAddItemTypeDialog = false
                pendingBookmarkTitle = ""
                pendingBookmarkUrl = ""
                showAddBookmarkDialog = true
            },
        )
    }

    if (showAddFolderDialog) {
        AddFolderDialog(
            folderTitle = pendingFolderTitle,
            onFolderTitleChange = { pendingFolderTitle = it },
            onDismiss = { showAddFolderDialog = false },
            onConfirm = {
                onAddFolder(pendingFolderTitle)
                showAddFolderDialog = false
            },
        )
    }

    if (showAddBookmarkDialog) {
        AddBookmarkDialog(
            bookmarkTitle = pendingBookmarkTitle,
            bookmarkUrl = pendingBookmarkUrl,
            onBookmarkTitleChange = { pendingBookmarkTitle = it },
            onBookmarkUrlChange = { pendingBookmarkUrl = it },
            onDismiss = { showAddBookmarkDialog = false },
            onConfirm = {
                onAddBookmark(pendingBookmarkTitle, pendingBookmarkUrl)
                showAddBookmarkDialog = false
            },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isImporting) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                trackColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    HomeTopAppBar(
                        isEditMode = isBrowserEditMode,
                        bookmarkDisplayType = state.bookmarkDisplayType,
                        defaultBrowserIcon = defaultBrowserIcon,
                        onBookmarkDisplayTypeClick = {
                            onBookmarkDisplayTypeToggle()
                        },
                        onDefaultBrowserIconClick = {
                            if (state.installedBrowsers.isNotEmpty()) {
                                showDefaultBrowserDialog = true
                            }
                        },
                        onSettingsClick = onSettingsClick,
                        onEditLabelClick = {
                            showColorPickerDialog = true
                        },
                        onEditModeDoneClick = {
                            isBrowserEditMode = false
                        },
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            showAddItemTypeDialog = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_bookmark_or_folder),
                        )
                    }
                },
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    BookMarkListBar(
                        orderedSnapshotIds = orderedSnapshotIds,
                        bookmarkColors = state.bookmarkColors,
                        selectedBookmarkId = selectedBookmarkId,
                        isEditMode = isBrowserEditMode,
                        onAddClick = {
                            showImportOptionDialog = true
                        },
                        onSnapshotClick = { snapshotId ->
                            val targetPage = orderedSnapshotIds.indexOf(snapshotId)
                            if (targetPage >= 0 && targetPage != pagerState.currentPage) {
                                scope.launch {
                                    pagerState.animateScrollToPage(targetPage)
                                }
                            }
                        },
                        onEnterEditMode = {
                            isBrowserEditMode = true
                        },
                        onDeleteRequest = { snapshotId ->
                            pendingDeleteSnapshotId = snapshotId
                        },
                    )

                    if (orderedSnapshotIds.isEmpty()) {
                        NoBookmarkItem(
                            modifier = Modifier.weight(1f),
                            onImportClick = {
                                showImportOptionDialog = true
                            },
                        )
                    } else {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.weight(1f),
                        ) { page ->
                            val snapshotId = orderedSnapshotIds[page]
                            val bookmarkDocument = state.bookmarkDocuments[snapshotId]
                            if (bookmarkDocument != null) {
                                BookmarkContent(
                                    modifier = Modifier.fillMaxSize(),
                                    bookmarkDocument = bookmarkDocument,
                                    displayType = state.bookmarkDisplayType,
                                    folderIconShape = state.folderIconShape,
                                    folderIconColor = state.folderIconColor,
                                    onBookmarkClick = { url ->
                                        if (!onOpenBookmark(url, state.defaultBrowserPackage)) {
                                            context.showShortToast(
                                                resources.getString(R.string.open_bookmark_failed),
                                            )
                                        }
                                    },
                                    onItemLongClick = { _, path ->
                                        pendingDeleteBookmarkItemPath = path
                                    },
                                )
                            } else {
                                NoBookmarkItem(
                                    modifier = Modifier.fillMaxSize(),
                                    onImportClick = {
                                        showImportOptionDialog = true
                                    },
                                )
                            }
                        }
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
