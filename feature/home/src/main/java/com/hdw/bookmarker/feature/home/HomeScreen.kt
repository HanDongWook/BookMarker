package com.hdw.bookmarker.feature.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.hdw.bookmarker.feature.home.dialog.BookmarkColorPickerDialog
import com.hdw.bookmarker.feature.home.dialog.DefaultBrowserPickerDialog
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
        AlertDialog(
            onDismissRequest = { showImportOptionDialog = false },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = {
                            showImportOptionDialog = false
                            onOpenBookmarkImportGuide()
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.import_option_dialog_open_guide))
                    }

                    OutlinedButton(
                        onClick = {
                            showImportOptionDialog = false
                            onOpenFilePicker()
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.import_option_dialog_pick_file))
                    }
                }
            },
            confirmButton = {},
            dismissButton = {},
        )
    }

    if (pendingDeleteSnapshotId != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteSnapshotId = null },
            title = { Text(text = stringResource(R.string.delete_bookmark_dialog_title)) },
            text = { Text(text = stringResource(R.string.delete_bookmark_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteBookmarkSnapshot(pendingDeleteSnapshotId ?: return@TextButton)
                        pendingDeleteSnapshotId = null
                    },
                ) {
                    Text(text = stringResource(R.string.delete_bookmark_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteSnapshotId = null }) {
                    Text(text = stringResource(R.string.delete_bookmark_dialog_cancel))
                }
            },
        )
    }

    if (showAddItemTypeDialog) {
        AlertDialog(
            onDismissRequest = { showAddItemTypeDialog = false },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = {
                            showAddItemTypeDialog = false
                            pendingFolderTitle = ""
                            showAddFolderDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.add_folder))
                    }

                    OutlinedButton(
                        onClick = {
                            showAddItemTypeDialog = false
                            pendingBookmarkTitle = ""
                            pendingBookmarkUrl = ""
                            showAddBookmarkDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.add_bookmark))
                    }
                }
            },
            confirmButton = {},
            dismissButton = {},
        )
    }

    if (showAddFolderDialog) {
        AlertDialog(
            onDismissRequest = { showAddFolderDialog = false },
            title = { Text(text = stringResource(R.string.add_folder)) },
            text = {
                OutlinedTextField(
                    value = pendingFolderTitle,
                    onValueChange = { pendingFolderTitle = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = stringResource(R.string.folder_name)) },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAddFolder(pendingFolderTitle)
                        showAddFolderDialog = false
                    },
                    enabled = pendingFolderTitle.isNotBlank(),
                ) {
                    Text(text = stringResource(R.string.add))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFolderDialog = false }) {
                    Text(text = stringResource(android.R.string.cancel))
                }
            },
        )
    }

    if (showAddBookmarkDialog) {
        AlertDialog(
            onDismissRequest = { showAddBookmarkDialog = false },
            title = { Text(text = stringResource(R.string.add_bookmark)) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = pendingBookmarkTitle,
                        onValueChange = { pendingBookmarkTitle = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = stringResource(R.string.bookmark_name)) },
                    )
                    OutlinedTextField(
                        value = pendingBookmarkUrl,
                        onValueChange = { pendingBookmarkUrl = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = stringResource(R.string.bookmark_url)) },
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAddBookmark(pendingBookmarkTitle, pendingBookmarkUrl)
                        showAddBookmarkDialog = false
                    },
                    enabled = pendingBookmarkTitle.isNotBlank() && pendingBookmarkUrl.isNotBlank(),
                ) {
                    Text(text = stringResource(R.string.add))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBookmarkDialog = false }) {
                    Text(text = stringResource(android.R.string.cancel))
                }
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
                        NoConnectedBrowsers(
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
                                )
                            } else {
                                NoConnectedBrowsers(
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
private fun NoConnectedBrowsers(modifier: Modifier, onImportClick: () -> Unit) {
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
