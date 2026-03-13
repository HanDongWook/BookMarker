package com.hdw.bookmarker.feature.settings.ui
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.url.AppWebUrl
import com.hdw.bookmarker.feature.settings.model.DisplayValueState
import com.hdw.bookmarker.feature.settings.ui.component.SettingsRow
import com.hdw.bookmarker.feature.settings.ui.tab.appversion.AppUpdateUiState
import com.hdw.bookmarker.feature.settings.ui.tab.appversion.AppVersionRow
import com.hdw.bookmarker.feature.settings.ui.tab.defaultbrowser.DefaultBrowserRow
import com.hdw.bookmarker.feature.settings.ui.tab.legal.PrivacyPolicyRow
import com.hdw.bookmarker.feature.settings.ui.tab.opensource.OpenSourceLicenseRow
import com.hdw.bookmarker.feature.settings.ui.tab.rateapp.RateAppRow
import com.hdw.bookmarker.feature.settings.ui.tab.rateapp.requestInAppReview
import com.hdw.bookmarker.feature.settings.ui.tab.shareapp.ShareAppRow
import com.hdw.bookmarker.feature.settings.ui.tab.shareapp.requestAppShare
import com.hdw.bookmarker.feature.settings.ui.tab.temporarydata.TemporaryDataRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    appVersion: DisplayValueState,
    temporaryDataSize: DisplayValueState,
    selectedBrowserName: String,
    selectedBrowserIcon: Any?,
    onTemporaryDataClick: () -> Unit,
    onDefaultBrowserClick: () -> Unit,
    onAppearanceClick: () -> Unit,
    onOpenSourceLicensesClick: () -> Unit,
    appUpdateUiState: AppUpdateUiState,
    onAppUpdateClick: () -> Unit,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val resources = LocalResources.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = resources.getString(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            DefaultBrowserRow(
                browserName = selectedBrowserName,
                browserIcon = selectedBrowserIcon,
                onClick = onDefaultBrowserClick,
            )
            BookMarkerDivider()

            SettingsRow(
                title = stringResource(R.string.appearance_label),
                onClick = onAppearanceClick,
            )
            BookMarkerDivider()

            TemporaryDataRow(
                size = temporaryDataSize,
                onClick = onTemporaryDataClick,
            )
            BookMarkerDivider()

            OpenSourceLicenseRow(
                onClick = onOpenSourceLicensesClick,
            )
            BookMarkerDivider()

            RateAppRow(
                onClick = { requestInAppReview(context) },
            )
            BookMarkerDivider()

            ShareAppRow(
                onClick = { requestAppShare(context) },
            )
            BookMarkerDivider()

            PrivacyPolicyRow(
                onClick = { uriHandler.openUri(AppWebUrl.PRIVACY_POLICY_URL) },
            )
            BookMarkerDivider()

            AppVersionRow(
                version = appVersion,
                appUpdateUiState = appUpdateUiState,
                onUpdateClick = onAppUpdateClick,
            )
            BookMarkerDivider()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        onBackClick = {},
        appVersion = DisplayValueState.Loaded("1.0.0"),
        temporaryDataSize = DisplayValueState.Loaded("12.3 MB"),
        selectedBrowserName = "Chrome",
        selectedBrowserIcon = null,
        onTemporaryDataClick = {},
        onDefaultBrowserClick = {},
        onAppearanceClick = {},
        onOpenSourceLicensesClick = {},
        appUpdateUiState = AppUpdateUiState.UpToDate,
        onAppUpdateClick = {},
    )
}
