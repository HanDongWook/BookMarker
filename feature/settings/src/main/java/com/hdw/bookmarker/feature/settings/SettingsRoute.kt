package com.hdw.bookmarker.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.hdw.bookmarker.core.ui.util.clearTemporaryData
import com.hdw.bookmarker.core.ui.util.getAppVersionDisplay
import com.hdw.bookmarker.feature.settings.model.DisplayValueState
import com.hdw.bookmarker.feature.settings.navigation.SettingsNavHost
import com.hdw.bookmarker.feature.settings.ui.tab.appversion.rememberAppUpdateController
import com.hdw.bookmarker.feature.settings.ui.tab.temporarydata.ClearTemporaryDataDialog
import com.hdw.bookmarker.feature.settings.ui.tab.temporarydata.loadTemporaryDataSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SettingsRoute(onBackClick: () -> Unit) {
    val settingsViewModel: SettingsViewModel = mavericksViewModel()
    val settingsState by settingsViewModel.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val appUpdateController = rememberAppUpdateController(
        appUpdateUiState = settingsState.appUpdateUiState,
        onUiStateChange = settingsViewModel::setAppUpdateUiState,
    )

    var temporaryDataSize by remember { mutableStateOf<DisplayValueState>(DisplayValueState.Loading) }
    var showClearTemporaryDataDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(context) {
        settingsViewModel.initialize(
            appVersion = context.getAppVersionDisplay(),
        )
        temporaryDataSize = loadTemporaryDataSize(context)
        appUpdateController.refresh(resumeInProgress = true)
    }

    SettingsNavHost(
        settingsState = settingsState,
        temporaryDataSize = temporaryDataSize,
        onBackClick = onBackClick,
        onTemporaryDataClick = { showClearTemporaryDataDialog = true },
        onAppUpdateClick = appUpdateController::onUpdateClick,
        onDefaultBrowserSelect = settingsViewModel::selectDefaultBrowser,
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
                    temporaryDataSize = loadTemporaryDataSize(context)
                }
            },
        )
    }
}
