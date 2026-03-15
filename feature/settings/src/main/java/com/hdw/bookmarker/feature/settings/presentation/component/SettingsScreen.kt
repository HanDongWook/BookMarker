package com.hdw.bookmarker.feature.settings.presentation.component
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.url.AppWebUrl
import com.hdw.bookmarker.feature.settings.presentation.model.DisplayValueState
import com.hdw.bookmarker.feature.settings.presentation.component.SettingsRow
import com.hdw.bookmarker.feature.settings.presentation.component.tab.appversion.AppUpdateUiState
import com.hdw.bookmarker.feature.settings.presentation.component.tab.appversion.AppVersionRow
import com.hdw.bookmarker.feature.settings.presentation.component.tab.development.DevelopmentRow
import com.hdw.bookmarker.feature.settings.presentation.component.tab.defaultbrowser.DefaultBrowserRow
import com.hdw.bookmarker.feature.settings.presentation.component.tab.legal.PrivacyPolicyRow
import com.hdw.bookmarker.feature.settings.presentation.component.tab.opensource.OpenSourceLicenseRow
import com.hdw.bookmarker.feature.settings.presentation.component.tab.rateapp.RateAppRow
import com.hdw.bookmarker.feature.settings.presentation.component.tab.rateapp.requestInAppReview
import com.hdw.bookmarker.feature.settings.presentation.component.tab.shareapp.ShareAppRow
import com.hdw.bookmarker.feature.settings.presentation.component.tab.shareapp.requestAppShare
import com.hdw.bookmarker.feature.settings.presentation.component.tab.temporarydata.TemporaryDataRow

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
    onBehaviorClick: () -> Unit,
    onOpenSourceLicensesClick: () -> Unit,
    appUpdateUiState: AppUpdateUiState,
    onAppUpdateClick: () -> Unit,
    showDevelopmentTab: Boolean,
    onDevelopmentClick: () -> Unit,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings)) },
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

            SettingsRow(
                title = stringResource(R.string.behavior_label),
                onClick = onBehaviorClick,
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

            if (showDevelopmentTab) {
                DevelopmentRow(
                    onClick = onDevelopmentClick,
                )
                BookMarkerDivider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun SettingsScreenPreview() {
    SettingsScreen(
        onBackClick = {},
        appVersion = DisplayValueState.Loaded("1.0.0"),
        temporaryDataSize = DisplayValueState.Loaded("12.3 MB"),
        selectedBrowserName = "Chrome",
        selectedBrowserIcon = null,
        onTemporaryDataClick = {},
        onDefaultBrowserClick = {},
        onAppearanceClick = {},
        onBehaviorClick = {},
        onOpenSourceLicensesClick = {},
        appUpdateUiState = AppUpdateUiState.UpToDate,
        onAppUpdateClick = {},
        showDevelopmentTab = true,
        onDevelopmentClick = {},
    )
}
