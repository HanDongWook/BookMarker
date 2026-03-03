package com.hdw.bookmarker.feature.settingsetting

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalUriHandler
import androidx.core.os.LocaleListCompat
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.core.ui.util.clearTemporaryData
import com.hdw.bookmarker.core.ui.util.getAppVersionDisplay
import com.hdw.bookmarker.core.ui.util.getTemporaryDataSizeDisplay
import com.hdw.bookmarker.feature.settingsetting.appversion.AppVersionRow
import com.hdw.bookmarker.feature.settingsetting.defaultbrowser.DefaultBrowserRow
import com.hdw.bookmarker.feature.settingsetting.folderstyle.FolderStyleRow
import com.hdw.bookmarker.feature.settingsetting.language.AppLanguageDialog
import com.hdw.bookmarker.feature.settingsetting.language.AppLanguageRow
import com.hdw.bookmarker.feature.settingsetting.legal.PrivacyPolicyRow
import com.hdw.bookmarker.feature.settingsetting.opensource.OpenSourceLicenseRow
import com.hdw.bookmarker.feature.settingsetting.rateapp.RateAppRow
import com.hdw.bookmarker.feature.settingsetting.rateapp.requestInAppReview
import com.hdw.bookmarker.feature.settingsetting.temporarydata.ClearTemporaryDataDialog
import com.hdw.bookmarker.feature.settingsetting.temporarydata.TemporaryDataRow
import com.hdw.bookmarker.feature.settingsetting.theme.ThemeModeDialog
import com.hdw.bookmarker.feature.settingsetting.theme.ThemeModeRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SettingsRoute(onBackClick: () -> Unit) {
    val viewModel: SettingsViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var temporaryDataSize by rememberSaveable { mutableStateOf("0 MB") }
    var showClearTemporaryDataDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(context) {
        viewModel.initialize(
            appVersion = context.getAppVersionDisplay(),
        )
        temporaryDataSize = withContext(Dispatchers.IO) {
            context.getTemporaryDataSizeDisplay()
        }
    }

    SettingsNavHost(
        state = state,
        temporaryDataSize = temporaryDataSize,
        onBackClick = onBackClick,
        onTemporaryDataClick = { showClearTemporaryDataDialog = true },
        onThemeModeSelect = viewModel::selectAppThemeMode,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    appVersion: String,
    temporaryDataSize: String,
    selectedThemeMode: String?,
    selectedBrowserName: String,
    selectedBrowserIcon: Any?,
    onTemporaryDataClick: () -> Unit,
    onDefaultBrowserClick: () -> Unit,
    selectedFolderIconShape: BookmarkFolderIconShape,
    selectedFolderIconColor: BookmarkFolderIconColor,
    onFolderStyleClick: () -> Unit,
    onOpenSourceLicensesClick: () -> Unit,
    onThemeModeSelect: (String) -> Unit,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val resources = LocalResources.current
    val isSystemDarkTheme = isSystemInDarkTheme()
    val effectiveThemeMode = selectedThemeMode ?: if (isSystemDarkTheme) {
        SettingsRepository.APP_THEME_MODE_DARK
    } else {
        SettingsRepository.APP_THEME_MODE_LIGHT
    }
    var selectedLanguageTag by rememberSaveable {
        mutableStateOf(AppCompatDelegate.getApplicationLocales().toLanguageTags().substringBefore(","))
    }
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    var showAppLanguageDialog by rememberSaveable { mutableStateOf(false) }

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

            ThemeModeRow(
                selectedThemeMode = effectiveThemeMode,
                onClick = { showThemeDialog = true },
            )
            BookMarkerDivider()

            AppLanguageRow(
                languageTag = selectedLanguageTag,
                onClick = { showAppLanguageDialog = true },
            )
            BookMarkerDivider()

            FolderStyleRow(
                shape = selectedFolderIconShape,
                color = selectedFolderIconColor,
                onClick = onFolderStyleClick,
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

            PrivacyPolicyRow(
                onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) },
            )
            BookMarkerDivider()

            AppVersionRow(version = appVersion)
            BookMarkerDivider()
        }
    }

    if (showThemeDialog) {
        ThemeModeDialog(
            selectedThemeMode = effectiveThemeMode,
            onDismiss = { showThemeDialog = false },
            onThemeModeSelect = { mode ->
                onThemeModeSelect(mode)
                showThemeDialog = false
            },
        )
    }

    if (showAppLanguageDialog) {
        AppLanguageDialog(
            selectedLanguageTag = selectedLanguageTag,
            onDismiss = { showAppLanguageDialog = false },
            onLanguageSelect = { languageTag ->
                selectedLanguageTag = languageTag
                val localeList = if (languageTag.isBlank()) {
                    LocaleListCompat.getEmptyLocaleList()
                } else {
                    LocaleListCompat.forLanguageTags(languageTag)
                }
                AppCompatDelegate.setApplicationLocales(localeList)
                showAppLanguageDialog = false
            },
        )
    }
}

private const val PRIVACY_POLICY_URL = "https://example.com/privacy-policy"
