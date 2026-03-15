package com.hdw.bookmarker.feature.settings.presentation.component.tab.appversion

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.findActivity
import com.hdw.bookmarker.core.ui.util.showShortToast
import kotlinx.coroutines.launch

internal class AppUpdateController(
    private val refreshAction: (Boolean) -> Unit,
    private val updateClickAction: () -> Unit,
) {
    fun refresh(resumeInProgress: Boolean = false) {
        refreshAction(resumeInProgress)
    }

    fun onUpdateClick() {
        updateClickAction()
    }
}

@Composable
internal fun rememberAppUpdateController(
    appUpdateUiState: AppUpdateUiState,
    onUiStateChange: (AppUpdateUiState) -> Unit,
): AppUpdateController {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val appUpdateManager = remember(context) { AppUpdateManagerFactory.create(context) }
    var pendingAppUpdateInfo by remember { mutableStateOf<AppUpdateInfo?>(null) }
    val appUpdateLauncherHolder = remember { mutableStateOf<ActivityResultLauncher<IntentSenderRequest>?>(null) }
    val latestAppUpdateUiState by rememberUpdatedState(appUpdateUiState)
    val latestOnUiStateChange by rememberUpdatedState(onUiStateChange)

    fun launchImmediateUpdate(appUpdateInfo: AppUpdateInfo?) {
        appUpdateLauncherHolder.value?.let { launcher ->
            launchImmediateUpdate(
                context = context,
                appUpdateManager = appUpdateManager,
                appUpdateInfo = appUpdateInfo,
                launcher = launcher,
                onUiStateChange = latestOnUiStateChange,
            )
        }
    }

    fun refreshAppUpdateState(resumeInProgress: Boolean) {
        coroutineScope.launch {
            val updateStateResult = requestAppUpdateState(appUpdateManager)
            pendingAppUpdateInfo = updateStateResult.pendingUpdateInfo
            latestOnUiStateChange(updateStateResult.uiState)
            if (resumeInProgress && updateStateResult.uiState is AppUpdateUiState.InProgress) {
                launchImmediateUpdate(updateStateResult.pendingUpdateInfo)
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
        if (didStartUpdate) {
            latestOnUiStateChange(AppUpdateUiState.InProgress)
        }
        refreshAppUpdateState(resumeInProgress = false)
    }
    appUpdateLauncherHolder.value = appUpdateLauncher

    DisposableEffect(lifecycleOwner, appUpdateManager) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshAppUpdateState(resumeInProgress = true)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return remember(appUpdateManager) {
        AppUpdateController(
            refreshAction = ::refreshAppUpdateState,
            updateClickAction = {
                if (
                    latestAppUpdateUiState is AppUpdateUiState.UpdateAvailable ||
                    latestAppUpdateUiState is AppUpdateUiState.InProgress
                ) {
                    coroutineScope.launch {
                        val updateInfo =
                            pendingAppUpdateInfo ?: requestAppUpdateState(appUpdateManager).also { result ->
                                pendingAppUpdateInfo = result.pendingUpdateInfo
                                latestOnUiStateChange(result.uiState)
                            }.pendingUpdateInfo
                        launchImmediateUpdate(updateInfo)
                    }
                }
            },
        )
    }
}

private fun launchImmediateUpdate(
    context: android.content.Context,
    appUpdateManager: AppUpdateManager,
    appUpdateInfo: AppUpdateInfo?,
    launcher: ActivityResultLauncher<IntentSenderRequest>,
    onUiStateChange: (AppUpdateUiState) -> Unit,
) {
    if (context.findActivity() == null || appUpdateInfo == null) {
        openPlayStoreForUpdate(context)
        onUiStateChange(AppUpdateUiState.Unavailable)
        return
    }

    val didStartUpdate = startImmediateAppUpdate(
        appUpdateManager = appUpdateManager,
        appUpdateInfo = appUpdateInfo,
        launcher = launcher,
    )
    if (!didStartUpdate) {
        openPlayStoreForUpdate(context)
        context.showShortToast(R.string.app_update_flow_failed)
    }
    onUiStateChange(if (didStartUpdate) AppUpdateUiState.InProgress else AppUpdateUiState.Unavailable)
}
