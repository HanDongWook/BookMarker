package com.hdw.bookmarker.feature.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.model.MimeTypes
import com.hdw.bookmarker.feature.home.drawer.DrawerContent
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSettingsClick: () -> Unit
) {
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
                        Toast.LENGTH_SHORT
                    ).show()
            }

            is MainSideEffect.ShowError -> {
                val message = context.getString(sideEffect.messageResId)
                val toastText = if (sideEffect.detail.isNullOrBlank()) {
                    message
                } else {
                    context.getString(R.string.home_error_with_detail, message, sideEffect.detail)
                }
                Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
            }

            is MainSideEffect.OpenFilePicker -> {
                htmlPickerLauncher.launch(arrayOf(MimeTypes.HTML))
            }
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val drawerWidth = (configuration.screenWidthDp * 0.7).dp

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(drawerWidth)
                    .fillMaxHeight()
            ) {
                DrawerContent(
                    installedBrowsers = state.installedBrowsers,
                    onSyncClick = viewModel::onSyncClick
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                HomeTopAppBar(
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onSettingsClick = onSettingsClick
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                ConnectedBrowserBar(
                    installedBrowsers = state.installedBrowsers,
                    connectedBrowserPackages = state.connectedBrowserPackages
                )
                HomContent(
                    bookmarksEmpty = state.bookmarks.isEmpty(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HomContent(
    bookmarksEmpty: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        if (bookmarksEmpty) {
            Text(
                text = stringResource(R.string.home_no_browsers_connected),
                textAlign = TextAlign.Center
            )
        }
    }
}
