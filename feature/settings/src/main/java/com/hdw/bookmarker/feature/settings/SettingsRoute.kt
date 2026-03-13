package com.hdw.bookmarker.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.hdw.bookmarker.core.ui.util.clearTemporaryData
import com.hdw.bookmarker.core.ui.util.getAppVersionDisplay
import com.hdw.bookmarker.core.ui.util.getTemporaryDataSizeDisplay
import com.hdw.bookmarker.feature.settings.navigation.SettingsNavHost
import com.hdw.bookmarker.feature.settings.ui.tab.appversion.rememberAppUpdateController
import com.hdw.bookmarker.feature.settings.ui.tab.temporarydata.ClearTemporaryDataDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SettingsRoute(onBackClick: () -> Unit) {
    val viewModel: SettingsViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val appUpdateController = rememberAppUpdateController(
        appUpdateUiState = state.appUpdateUiState,
        onUiStateChange = viewModel::setAppUpdateUiState,
    )

    var temporaryDataSize by rememberSaveable { mutableStateOf("0 MB") }
    var showClearTemporaryDataDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(context) {
        viewModel.initialize(
            appVersion = context.getAppVersionDisplay(),
        )
        temporaryDataSize = withContext(Dispatchers.IO) {
            context.getTemporaryDataSizeDisplay()
        }
        appUpdateController.refresh(resumeInProgress = true)
    }

    SettingsNavHost(
        state = state,
        temporaryDataSize = temporaryDataSize,
        onBackClick = onBackClick,
        onTemporaryDataClick = { showClearTemporaryDataDialog = true },
        onAppUpdateClick = appUpdateController::onUpdateClick,
        onScrollLongBookmarkUrlChange = viewModel::setScrollLongBookmarkUrl,
        onShowFolderDescriptionChange = viewModel::setShowFolderDescription,
        onScrollLongFolderDescriptionChange = viewModel::setScrollLongFolderDescription,
        onThemeModeSelect = viewModel::selectAppThemeMode,
        onShowBookmarkUrlChange = viewModel::setShowBookmarkUrl,
        onDefaultBrowserSelect = viewModel::selectDefaultBrowser,
        onFolderShapeSelect = viewModel::selectFolderIconShape,
        onFolderColorSelect = viewModel::selectFolderIconColor,
    )

    if (showClearTemporaryDataDialog) {
        ClearTemporaryDataDialog(
            onDismiss = { showClearTemporaryDataDialog = false },
            onDelete = {
                showClearTemporaryDataDialog = false
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        context.clearTemporaryData()
                    }
                    temporaryDataSize = withContext(Dispatchers.IO) {
                        context.getTemporaryDataSizeDisplay()
                    }
                }
            },
        )
    }
}
