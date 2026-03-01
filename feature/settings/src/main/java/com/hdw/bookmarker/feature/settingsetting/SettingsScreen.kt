package com.hdw.bookmarker.feature.settingsetting

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.getAppVersionName
import com.hdw.bookmarker.feature.settingsetting.appversion.AppVersionRow
import com.hdw.bookmarker.feature.settingsetting.defaultbrowser.DefaultBrowserRow
import com.hdw.bookmarker.feature.settingsetting.theme.ThemeModeDialog
import com.hdw.bookmarker.feature.settingsetting.theme.ThemeModeRow

@Composable
fun SettingsRoute(onBackClick: () -> Unit, onDefaultBrowserClick: () -> Unit) {
    val viewModel: SettingsViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(context) {
        viewModel.initialize(
            appVersion = context.getAppVersionName(),
        )
    }

    SettingsScreen(
        onBackClick = onBackClick,
        appVersion = state.appVersion,
        selectedThemeMode = state.selectedThemeMode,
        selectedBrowserName =
        state.installedBrowsers.firstOrNull { it.packageName == state.selectedBrowserPackage }?.appName
            ?: stringResource(R.string.not_selected),
        selectedBrowserIcon = state.installedBrowsers.firstOrNull {
            it.packageName == state.selectedBrowserPackage
        }?.icon,
        onDefaultBrowserClick = onDefaultBrowserClick,
        onThemeModeSelect = viewModel::selectAppThemeMode,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    appVersion: String,
    selectedThemeMode: String?,
    selectedBrowserName: String,
    selectedBrowserIcon: Any?,
    onDefaultBrowserClick: () -> Unit,
    onThemeModeSelect: (String) -> Unit,
) {
    val resources = LocalResources.current
    val isSystemDarkTheme = isSystemInDarkTheme()
    val effectiveThemeMode = selectedThemeMode ?: if (isSystemDarkTheme) {
        SettingsRepository.APP_THEME_MODE_DARK
    } else {
        SettingsRepository.APP_THEME_MODE_LIGHT
    }
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }

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
}
