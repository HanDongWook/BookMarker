package com.hdw.bookmarker.feature.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSettingsClick: () -> Unit,
    onOpenDesktopGuide: () -> Boolean,
    onOpenBookmark: (String) -> Boolean,
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val resources = LocalResources.current
    var showImportGuideDialog by rememberSaveable { mutableStateOf(false) }
    var showOverwriteConfirmDialog by rememberSaveable { mutableStateOf(false) }

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
        pageCount = { state.installedBrowsers.size },
    )
    val selectedBrowserIcon = state.installedBrowsers.getOrNull(pagerState.currentPage)?.icon
    val currentSelectedBrowserIcon = state.installedBrowsers
        .firstOrNull { it.packageName == state.selectedBrowserPackage }
        ?.icon ?: selectedBrowserIcon
    val importIconSharedKey = "import_guide_icon_${state.selectedBrowserPackage ?: "unknown"}"

    LaunchedEffect(pagerState, state.installedBrowsers) {
        if (state.installedBrowsers.isEmpty()) return@LaunchedEffect
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                state.installedBrowsers.getOrNull(page)?.packageName?.let(viewModel::onBrowserSelected)
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

    SharedTransitionLayout {
        AnimatedContent(
            targetState = showImportGuideDialog,
            label = "bookmarkImportGuideTransition",
        ) { isGuideVisible ->
            if (!isGuideVisible) {
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
                                onSyncClick = {
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
                                onMenuClick = {
                                    scope.launch { drawerState.open() }
                                },
                                onSettingsClick = onSettingsClick,
                            )
                        },
                    ) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            ConnectedBrowserBar(
                                installedBrowsers = state.installedBrowsers,
                                connectedBrowserPackages = state.connectedBrowserPackages,
                                selectedBrowserPackage = state.selectedBrowserPackage,
                                onBrowserClick = { packageName ->
                                    val targetPage = state.installedBrowsers
                                        .indexOfFirst { it.packageName == packageName }
                                    if (targetPage >= 0 && targetPage != pagerState.currentPage) {
                                        scope.launch {
                                            pagerState.animateScrollToPage(targetPage)
                                        }
                                    }
                                },
                            )

                            if (state.installedBrowsers.isEmpty()) {
                                NoConnectedBrowsers(modifier = Modifier.weight(1f))
                            } else {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.weight(1f),
                                ) { page ->
                                    val browser = state.installedBrowsers[page]
                                    BookmarkContent(
                                        modifier = Modifier.fillMaxSize(),
                                        bookmarkDocument = state.bookmarkDocuments[browser.packageName],
                                        selectedBrowserIcon = browser.icon,
                                        onImportClick = { showImportGuideDialog = true },
                                        onBookmarkClick = { url ->
                                            if (!onOpenBookmark(url)) {
                                                context.showShortToast(
                                                    resources.getString(R.string.open_bookmark_failed),
                                                )
                                            }
                                        },
                                        importIconModifier = Modifier.sharedElement(
                                            sharedContentState = rememberSharedContentState(key = importIconSharedKey),
                                            animatedVisibilityScope = this@AnimatedContent,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                BookmarkImportGuideScreen(
                    icon = currentSelectedBrowserIcon,
                    iconModifier = Modifier.sharedElement(
                        sharedContentState = rememberSharedContentState(key = importIconSharedKey),
                        animatedVisibilityScope = this@AnimatedContent,
                    ),
                    onDismiss = { showImportGuideDialog = false },
                    onOpenDesktopGuide = {
                        if (!onOpenDesktopGuide()) {
                            context.showShortToast(resources.getString(R.string.import_guide_open_guide_failed))
                        }
                    },
                    onSelectFile = {
                        val selectedPackage = state.selectedBrowserPackage
                            ?: state.installedBrowsers.getOrNull(pagerState.currentPage)?.packageName
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
    }
}

@Composable
private fun NoConnectedBrowsers(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.no_browsers_connected),
            textAlign = TextAlign.Center,
        )
    }
}
