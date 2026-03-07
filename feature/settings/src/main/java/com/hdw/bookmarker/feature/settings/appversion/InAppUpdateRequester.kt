package com.hdw.bookmarker.feature.settings.appversion

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.net.toUri
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.requestAppUpdateInfo
import com.hdw.bookmarker.core.ui.url.AppWebUrl
import timber.log.Timber

internal data class AppUpdateStateResult(val uiState: AppUpdateUiState, val pendingUpdateInfo: AppUpdateInfo?)

internal suspend fun requestAppUpdateState(appUpdateManager: AppUpdateManager): AppUpdateStateResult {
    val appUpdateInfo = runCatching {
        appUpdateManager.requestAppUpdateInfo()
    }.onFailure { throwable ->
        Timber.e(throwable, "Failed to request in-app update state")
    }.getOrNull() ?: return AppUpdateStateResult(
        uiState = AppUpdateUiState.Unavailable,
        pendingUpdateInfo = null,
    )

    val updateState = when {
        appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS &&
            appUpdateInfo.isImmediateUpdateAllowed -> {
            AppUpdateUiState.InProgress
        }

        appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
            appUpdateInfo.isImmediateUpdateAllowed -> {
            AppUpdateUiState.UpdateAvailable(
                availableVersionCode = appUpdateInfo.availableVersionCode(),
            )
        }

        appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
            AppUpdateUiState.UpToDate
        }

        else -> {
            val preconditions = appUpdateInfo.getFailedUpdatePreconditions(
                AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
            )
            Timber.d("In-app update unavailable preconditions: $preconditions")
            AppUpdateUiState.Unavailable
        }
    }

    val pendingUpdateInfo = when (updateState) {
        is AppUpdateUiState.UpdateAvailable,
        AppUpdateUiState.InProgress,
        -> appUpdateInfo

        else -> null
    }

    return AppUpdateStateResult(
        uiState = updateState,
        pendingUpdateInfo = pendingUpdateInfo,
    )
}

internal fun startImmediateAppUpdate(
    appUpdateManager: AppUpdateManager,
    appUpdateInfo: AppUpdateInfo,
    launcher: ActivityResultLauncher<IntentSenderRequest>,
): Boolean = runCatching<Boolean> {
    appUpdateManager.startUpdateFlowForResult(
        appUpdateInfo,
        launcher,
        AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
    )
}.onFailure { throwable ->
    Timber.e(throwable, "Failed to start immediate in-app update")
}.getOrDefault(false)

internal fun openPlayStoreForUpdate(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, AppWebUrl.PLAY_STORE_URL.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching {
        context.startActivity(intent)
    }.onFailure { throwable ->
        Timber.e(throwable, "Failed to open Play Store page")
    }
}
