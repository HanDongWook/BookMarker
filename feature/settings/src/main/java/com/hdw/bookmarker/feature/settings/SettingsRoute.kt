package com.hdw.bookmarker.feature.settings

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.clearTemporaryData
import com.hdw.bookmarker.core.ui.util.findActivity
import com.hdw.bookmarker.core.ui.util.getAppVersionDisplay
import com.hdw.bookmarker.core.ui.util.getTemporaryDataSizeDisplay
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.settings.navigation.SettingsNavHost
import com.hdw.bookmarker.feature.settings.ui.tab.appversion.openPlayStoreForUpdate
import com.hdw.bookmarker.feature.settings.ui.tab.appversion.startImmediateAppUpdate
import com.hdw.bookmarker.feature.settings.ui.tab.temporarydata.ClearTemporaryDataDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SettingsRoute(onBackClick: () -> Unit) {
    val viewModel: SettingsViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var temporaryDataSize by rememberSaveable { mutableStateOf("0 MB") }
    var showClearTemporaryDataDialog by rememberSaveable { mutableStateOf(false) }
    val appUpdateManager = remember(context) { AppUpdateManagerFactory.create(context) }
    var pendingAppUpdateInfo by remember { mutableStateOf<AppUpdateInfo?>(null) }
    var lastHandledUpdateLaunchRequestId by rememberSaveable { mutableLongStateOf(0L) }
    val refreshAppUpdateState: () -> Unit = remember(viewModel, appUpdateManager) {
        {
            viewModel.fetchAppUpdateState(appUpdateManager) { updateInfo ->
                pendingAppUpdateInfo = updateInfo
            }
        }
    }

    val appUpdateLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        val didStartUpdate = result.resultCode == Activity.RESULT_OK
        if (!didStartUpdate) {
            val toastMessageRes = if (result.resultCode == Activity.RESULT_CANCELED) {
                R.string.app_update_flow_canceled
            } else {
                R.string.app_update_flow_failed
            }
            context.showShortToast(toastMessageRes)
        }
        viewModel.onAppUpdateLaunchResult(isStarted = didStartUpdate)
        refreshAppUpdateState()
    }

    LaunchedEffect(context) {
        viewModel.initialize(
            appVersion = context.getAppVersionDisplay(),
        )
        temporaryDataSize = withContext(Dispatchers.IO) {
            context.getTemporaryDataSizeDisplay()
        }
        refreshAppUpdateState()
    }

    DisposableEffect(lifecycleOwner, appUpdateManager) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshAppUpdateState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state.updateLaunchRequestId) {
        val launchRequestId = state.updateLaunchRequestId
        if (launchRequestId == 0L || launchRequestId == lastHandledUpdateLaunchRequestId) return@LaunchedEffect
        lastHandledUpdateLaunchRequestId = launchRequestId

        val updateInfo = pendingAppUpdateInfo
        if (context.findActivity() == null || updateInfo == null) {
            openPlayStoreForUpdate(context)
            viewModel.onAppUpdateLaunchResult(isStarted = false)
            return@LaunchedEffect
        }

        val didStartUpdate = startImmediateAppUpdate(
            appUpdateManager = appUpdateManager,
            appUpdateInfo = updateInfo,
            launcher = appUpdateLauncher,
        )
        if (!didStartUpdate) {
            openPlayStoreForUpdate(context)
            context.showShortToast(R.string.app_update_flow_failed)
        }
        viewModel.onAppUpdateLaunchResult(isStarted = didStartUpdate)
    }

    SettingsNavHost(
        state = state,
        temporaryDataSize = temporaryDataSize,
        onBackClick = onBackClick,
        onTemporaryDataClick = { showClearTemporaryDataDialog = true },
        onAppUpdateClick = viewModel::onAppUpdateClick,
        onScrollLongBookmarkUrlChange = viewModel::setScrollLongBookmarkUrl,
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
