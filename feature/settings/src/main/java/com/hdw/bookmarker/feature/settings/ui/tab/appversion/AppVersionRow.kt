package com.hdw.bookmarker.feature.settings.ui.tab.appversion

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.model.DisplayValueState
import com.hdw.bookmarker.feature.settings.ui.component.SettingsRow

@Composable
fun AppVersionRow(version: DisplayValueState, appUpdateUiState: AppUpdateUiState, onUpdateClick: () -> Unit) {
    val updateStatusText = when (appUpdateUiState) {
        AppUpdateUiState.UpToDate -> stringResource(R.string.app_update_status_latest)

        is AppUpdateUiState.UpdateAvailable -> {
            stringResource(
                R.string.app_update_status_available,
                appUpdateUiState.availableVersionCode,
            )
        }

        AppUpdateUiState.Checking,
        AppUpdateUiState.InProgress,
        AppUpdateUiState.Unavailable,
        -> null
    }
    val versionText = when (version) {
        DisplayValueState.Loading -> stringResource(R.string.display_value_loading)
        is DisplayValueState.Loaded -> version.value
        DisplayValueState.Unavailable -> null
    }

    SettingsRow(
        title = stringResource(R.string.app_version_label),
        trailingContent = {
            versionText?.let {
                Text(
                    text = if (updateStatusText == null || version !is DisplayValueState.Loaded) {
                        it
                    } else {
                        "$it ($updateStatusText)"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        onClick = {
            if (appUpdateUiState is AppUpdateUiState.UpdateAvailable) {
                onUpdateClick()
            }
        },
    )
}

@Composable
private fun AppVersionRowPreviewContent(appUpdateUiState: AppUpdateUiState) {
    MaterialTheme {
        AppVersionRow(
            version = DisplayValueState.Loaded("1.2.3 (123)"),
            appUpdateUiState = appUpdateUiState,
            onUpdateClick = {},
        )
    }
}

@Preview(showBackground = true, name = "Checking")
@Composable
private fun AppVersionRowCheckingPreview() {
    AppVersionRowPreviewContent(appUpdateUiState = AppUpdateUiState.Checking)
}

@Preview(showBackground = true, name = "UpToDate")
@Composable
private fun AppVersionRowUpToDatePreview() {
    AppVersionRowPreviewContent(appUpdateUiState = AppUpdateUiState.UpToDate)
}

@Preview(showBackground = true, name = "UpdateAvailable")
@Composable
private fun AppVersionRowUpdateAvailablePreview() {
    AppVersionRowPreviewContent(appUpdateUiState = AppUpdateUiState.UpdateAvailable(availableVersionCode = 124))
}

@Preview(showBackground = true, name = "InProgress")
@Composable
private fun AppVersionRowInProgressPreview() {
    AppVersionRowPreviewContent(appUpdateUiState = AppUpdateUiState.InProgress)
}

@Preview(showBackground = true, name = "Unavailable")
@Composable
private fun AppVersionRowUnavailablePreview() {
    AppVersionRowPreviewContent(appUpdateUiState = AppUpdateUiState.Unavailable)
}
