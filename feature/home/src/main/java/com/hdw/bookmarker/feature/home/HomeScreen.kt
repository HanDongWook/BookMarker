package com.hdw.bookmarker.feature.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
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
import com.hdw.bookmarker.core.model.MimeTypes
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.home.appbar.HomeTopAppBar
import com.hdw.bookmarker.feature.home.drawer.HomeDrawerContent
import com.hdw.bookmarker.feature.home.guide.BookmarkImportGuideScreen
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeRoute(onSettingsClick: () -> Unit, onOpenDesktopGuide: () -> Boolean, onOpenBookmark: (String) -> Boolean) {
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
    onOpenDesktopGuide: () -> Boolean,
    onOpenBookmark: (String) -> Boolean,
) {
    val state by viewModel.collectAsState()
    val connectedBrowsers = state.installedBrowsers.filter { browser ->
        state.connectedBrowserPackages.contains(browser.packageName)
    }
    val context = LocalContext.current
    val resources = LocalResources.current
    var showImportGuideDialog by rememberSaveable { mutableStateOf(false) }
    var showOverwriteConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var isBrowserEditMode by rememberSaveable { mutableStateOf(false) }
    var pendingDeleteBrowserPackage by rememberSaveable { mutableStateOf<String?>(null) }

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

            MainSideEffect.ShowOverwriteConfirmDialog -> {
                showOverwriteConfirmDialog = true
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
        pageCount = { connectedBrowsers.size },
    )
    val selectedConnectedBrowserPackage = state.selectedBrowserPackage
        ?.takeIf { selected -> connectedBrowsers.any { it.packageName == selected } }
        ?: connectedBrowsers.getOrNull(pagerState.currentPage)?.packageName
        ?: connectedBrowsers.firstOrNull()?.packageName

    val currentSelectedBrowser = state.installedBrowsers
        .firstOrNull { it.packageName == state.selectedBrowserPackage }
        ?: connectedBrowsers.getOrNull(pagerState.currentPage)
        ?: state.installedBrowsers.firstOrNull()

    LaunchedEffect(pagerState, connectedBrowsers) {
        if (connectedBrowsers.isEmpty()) return@LaunchedEffect
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                connectedBrowsers.getOrNull(page)?.packageName?.let(viewModel::onBrowserSelected)
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

    BackHandler(enabled = isBrowserEditMode) {
        isBrowserEditMode = false
    }

    BackHandler(enabled = showOverwriteConfirmDialog) {
        showOverwriteConfirmDialog = false
        viewModel.cancelOverwriteImport()
    }

    if (showOverwriteConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showOverwriteConfirmDialog = false
                viewModel.cancelOverwriteImport()
            },
            title = {
                Text(text = stringResource(R.string.import_overwrite_dialog_title))
            },
            text = {
                Text(text = stringResource(R.string.import_overwrite_dialog_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showOverwriteConfirmDialog = false
                        viewModel.confirmOverwriteImport()
                    },
                ) {
                    Text(text = stringResource(R.string.import_overwrite_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showOverwriteConfirmDialog = false
                        viewModel.cancelOverwriteImport()
                    },
                ) {
                    Text(text = stringResource(R.string.import_overwrite_dialog_cancel))
                }
            },
        )
    }

    if (pendingDeleteBrowserPackage != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteBrowserPackage = null },
            title = { Text(text = stringResource(R.string.delete_bookmark_dialog_title)) },
            text = { Text(text = stringResource(R.string.delete_bookmark_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBookmarkSnapshot(pendingDeleteBrowserPackage ?: return@TextButton)
                        pendingDeleteBrowserPackage = null
                        isBrowserEditMode = false
                    },
                ) {
                    Text(text = stringResource(R.string.delete_bookmark_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteBrowserPackage = null }) {
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
                        connectedBrowserPackages = state.connectedBrowserPackages,
                        onSyncClick = { packageName ->
                            viewModel.onBrowserSelected(packageName)
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
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        },
                        onSettingsClick = onSettingsClick,
                        onEditModeDoneClick = {
                            isBrowserEditMode = false
                        },
                    )
                },
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    if (state.connectedBrowserPackages.isNotEmpty()) {
                        ConnectedBrowserBar(
                            installedBrowsers = state.installedBrowsers,
                            connectedBrowserPackages = state.connectedBrowserPackages,
                            selectedBrowserPackage = selectedConnectedBrowserPackage,
                            isEditMode = isBrowserEditMode,
                            onBrowserClick = { packageName ->
                                val targetPage = connectedBrowsers
                                    .indexOfFirst { it.packageName == packageName }
                                if (targetPage >= 0 && targetPage != pagerState.currentPage) {
                                    scope.launch {
                                        pagerState.animateScrollToPage(targetPage)
                                    }
                                }
                            },
                            onEnterEditMode = {
                                isBrowserEditMode = true
                            },
                            onDeleteRequest = { packageName ->
                                pendingDeleteBrowserPackage = packageName
                            },
                        )
                    }

                    if (connectedBrowsers.isEmpty()) {
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
                            val browser = connectedBrowsers[page]
                            BookmarkContent(
                                modifier = Modifier.fillMaxSize(),
                                bookmarkDocument = state.bookmarkDocuments.getValue(browser.packageName),
                                onBookmarkClick = { url ->
                                    if (!onOpenBookmark(url)) {
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
                if (!onOpenDesktopGuide()) {
                    context.showShortToast(resources.getString(R.string.import_guide_open_guide_failed))
                }
            },
            onSelectFile = {
                val selectedPackage = state.selectedBrowserPackage
                    ?: connectedBrowsers.getOrNull(pagerState.currentPage)?.packageName
                if (selectedPackage == null) {
                    context.showShortToast(resources.getString(R.string.no_browsers_connected))
                    return@BookmarkImportGuideScreen
                }
                viewModel.onBrowserSelected(selectedPackage)
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
