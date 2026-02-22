package com.hdw.bookmarker.feature.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.hdw.bookmarker.core.model.MimeTypes
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeScreen(viewModel: HomeViewModel, onSettingsClick: () -> Unit) {
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
            is MainSideEffect.ShowSyncStarted -> {
                Toast
                    .makeText(
                        context,
                        resources.getString(R.string.home_syncing, sideEffect.browserName),
                        Toast.LENGTH_SHORT,
                    ).show()
            }

            is MainSideEffect.ShowError -> {
                val message = resources.getString(sideEffect.messageResId)
                val toastText = if (sideEffect.detail.isNullOrBlank()) {
                    message
                } else {
                    resources.getString(R.string.home_error_with_detail, message, sideEffect.detail)
                }
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            }

            is MainSideEffect.OpenFilePicker -> {
                htmlPickerLauncher.launch(arrayOf(MimeTypes.HTML))
            }
        }
    }

    val scope = rememberCoroutineScope()
    var showImportGuideDialog by rememberSaveable { mutableStateOf(false) }
    var selectedBrowserPackage by rememberSaveable { mutableStateOf<String?>(null) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { state.installedBrowsers.size },
    )
    val selectedBrowserIcon = state.installedBrowsers.getOrNull(pagerState.currentPage)?.icon
    val chromeIcon = state.installedBrowsers.firstOrNull {
        it.packageName.equals("com.android.chrome", ignoreCase = true) ||
            it.packageName.contains("chrome", ignoreCase = true) ||
            it.appName.contains("chrome", ignoreCase = true)
    }?.icon ?: selectedBrowserIcon

    LaunchedEffect(pagerState, state.installedBrowsers) {
        if (state.installedBrowsers.isEmpty()) return@LaunchedEffect
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                selectedBrowserPackage = state.installedBrowsers.getOrNull(page)?.packageName
            }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { HomeTopAppBar(onSettingsClick = onSettingsClick) },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ConnectedBrowserBar(
                installedBrowsers = state.installedBrowsers,
                connectedBrowserPackages = emptySet(),
                selectedBrowserPackage = selectedBrowserPackage,
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
                        bookmarkDocument = null,
                        selectedBrowserIcon = browser.icon,
                        onImportClick = { showImportGuideDialog = true },
                    )
                }
            }
        }
    }

    if (showImportGuideDialog) {
        BookmarkImportGuideDialog(
            icon = chromeIcon,
            onDismiss = { showImportGuideDialog = false },
            onSelectFile = {
                showImportGuideDialog = false
                viewModel.openFilePicker()
            },
        )
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
            text = stringResource(R.string.home_no_browsers_connected),
            textAlign = TextAlign.Center,
        )
    }
}
