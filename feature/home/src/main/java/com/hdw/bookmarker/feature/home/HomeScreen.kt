package com.hdw.bookmarker.feature.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hdw.bookmarker.core.domain.util.BookmarkColorGenerator
import com.hdw.bookmarker.core.model.MimeTypes
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.home.appbar.HomeTopAppBar
import com.hdw.bookmarker.feature.home.boomarkcontent.BookmarkContent
import com.hdw.bookmarker.feature.home.dialog.BookmarkColorPickerDialog
import com.hdw.bookmarker.feature.home.dialog.DefaultBrowserPickerDialog
import com.hdw.bookmarker.feature.home.drawer.HomeDrawerContent
import com.hdw.bookmarker.feature.home.guide.BookmarkImportGuideScreen
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeRoute(
    onSettingsClick: () -> Unit,
    onOpenDesktopGuide: (Browser, String?) -> Boolean,
    onOpenBookmark: (String, String?) -> Boolean,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    HomeScreen(
        viewModel = viewModel,
        onSettingsClick = onSettingsClick,
        onOpenDesktopGuide = onOpenDesktopGuide,
        onOpenBookmark = onOpenBookmark,
    )
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSettingsClick: () -> Unit,
    onOpenDesktopGuide: (Browser, String?) -> Boolean,
    onOpenBookmark: (String, String?) -> Boolean,
) {
    val state by viewModel.collectAsState()
    val orderedSnapshotIds = state.orderedSnapshotIds
    val context = LocalContext.current
    val resources = LocalResources.current
    var showImportGuideDialog by rememberSaveable { mutableStateOf(false) }
    var showImportOptionDialog by rememberSaveable { mutableStateOf(false) }
    var isBrowserEditMode by rememberSaveable { mutableStateOf(false) }
    var showDefaultBrowserDialog by rememberSaveable { mutableStateOf(false) }
    var showColorPickerDialog by rememberSaveable { mutableStateOf(false) }
    var pendingDeleteSnapshotId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedBrowserPackageForImport by rememberSaveable { mutableStateOf<String?>(null) }

    val htmlPickerLauncher = rememberLauncherForActivityResult(OpenDocument()) { uri ->
        if (uri != null) {
            showImportGuideDialog = false
            viewModel.onHtmlFileSelected(uri)
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MainSideEffect.ShowError -> {
                val message = resources.getString(sideEffect.messageResId)
                val toastText = if (sideEffect.detail.isNullOrBlank()) {
                    message
                } else {
                    resources.getString(R.string.error_with_detail, message, sideEffect.detail)
                }
                context.showShortToast(toastText)
            }

            is MainSideEffect.ShowMessage -> {
                context.showShortToast(resources.getString(sideEffect.messageResId))
            }

            is MainSideEffect.OpenFilePicker -> {
                htmlPickerLauncher.launch(arrayOf(MimeTypes.HTML))
            }
        }
    }

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val configuration = LocalConfiguration.current
    val drawerWidth = (configuration.screenWidthDp * 0.7).dp

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { orderedSnapshotIds.size },
    )
    val selectedBookmarkId = state.selectedBookmarkId
        ?.takeIf { id -> orderedSnapshotIds.contains(id) }
        ?: orderedSnapshotIds.getOrNull(pagerState.currentPage)
        ?: orderedSnapshotIds.firstOrNull()

    val currentSelectedBrowser = selectedBrowserPackageForImport
        ?.let { pkg -> state.installedBrowsers.find { it.packageName == pkg } }
        ?: state.installedBrowsers.firstOrNull()
    val defaultBrowserIcon = state.installedBrowsers
        .firstOrNull { it.packageName == state.defaultBrowserPackage }
        ?.icon

    LaunchedEffect(pagerState, orderedSnapshotIds) {
        if (orderedSnapshotIds.isEmpty()) return@LaunchedEffect
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                orderedSnapshotIds.getOrNull(page)?.let(viewModel::onSnapshotSelected)
            }
    }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    BackHandler(enabled = showImportGuideDialog) {
        showImportGuideDialog = false
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

    if (showColorPickerDialog && selectedBookmarkId != null) {
        BookmarkColorPickerDialog(
            colors = BookmarkColorGenerator.getAllColors(),
            currentColor = state.bookmarkColors[selectedBookmarkId]
                ?: BookmarkColorGenerator.generateColorForId(selectedBookmarkId),
            onColorSelect = { color ->
                viewModel.onBookmarkColorSelected(selectedBookmarkId, color)
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
                viewModel.onDefaultBrowserSelected(packageName)
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
                            selectedBrowserPackageForImport = state.defaultBrowserPackage
                            showImportOptionDialog = false
                            showImportGuideDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.import_option_dialog_open_guide))
                    }

                    OutlinedButton(
                        onClick = {
                            showImportOptionDialog = false
                            viewModel.openFilePicker()
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
                        viewModel.deleteBookmarkSnapshot(pendingDeleteSnapshotId ?: return@TextButton)
                        pendingDeleteSnapshotId = null
                        isBrowserEditMode = false
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

    if (!showImportGuideDialog) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .width(drawerWidth)
                        .fillMaxHeight(),
                ) {
                    HomeDrawerContent(
                        installedBrowsers = state.installedBrowsers,
                        onSyncClick = { packageName ->
                            selectedBrowserPackageForImport = packageName
                            showImportGuideDialog = true
                            scope.launch { drawerState.close() }
                        },
                    )
                }
            },
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    HomeTopAppBar(
                        isEditMode = isBrowserEditMode,
                        bookmarkDisplayType = state.bookmarkDisplayType,
                        defaultBrowserIcon = defaultBrowserIcon,
                        onBookmarkDisplayTypeClick = {
                            viewModel.onBookmarkDisplayTypeToggle()
                        },
                        onDefaultBrowserIconClick = {
                            if (state.installedBrowsers.isNotEmpty()) {
                                showDefaultBrowserDialog = true
                            }
                        },
                        onMenuClick = {
                            scope.launch { drawerState.open() }
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
                                scope.launch { drawerState.open() }
                            },
                        )
                    } else {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.weight(1f),
                        ) { page ->
                            val snapshotId = orderedSnapshotIds[page]
                            BookmarkContent(
                                modifier = Modifier.fillMaxSize(),
                                bookmarkDocument = state.bookmarkDocuments.getValue(snapshotId),
                                displayType = state.bookmarkDisplayType,
                                onBookmarkClick = { url ->
                                    if (!onOpenBookmark(url, state.defaultBrowserPackage)) {
                                        context.showShortToast(
                                            resources.getString(R.string.open_bookmark_failed),
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    } else {
        BookmarkImportGuideScreen(
            icon = currentSelectedBrowser?.icon,
            browserPackageName = currentSelectedBrowser?.packageName,
            browserName = currentSelectedBrowser?.appName,
            onDismiss = { showImportGuideDialog = false },
            onOpenDesktopGuide = {
                val selectedBrowserType = Browser.fromPackageAndName(
                    packageName = currentSelectedBrowser?.packageName,
                    appName = currentSelectedBrowser?.appName,
                )
                if (!onOpenDesktopGuide(selectedBrowserType, currentSelectedBrowser?.packageName)) {
                    context.showShortToast(resources.getString(R.string.import_guide_open_guide_failed))
                }
            },
            onSelectFile = {
                viewModel.openFilePicker()
            },
        )
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
