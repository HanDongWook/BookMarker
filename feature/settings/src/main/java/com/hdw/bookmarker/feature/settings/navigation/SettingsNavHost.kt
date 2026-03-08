package com.hdw.bookmarker.feature.settings.navigation
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.navigation.slideComposable
import com.hdw.bookmarker.feature.settings.model.SettingsState
import com.hdw.bookmarker.feature.settings.ui.SettingsScreen
import com.hdw.bookmarker.feature.settings.ui.tab.appversion.AppUpdateUiState
import com.hdw.bookmarker.feature.settings.ui.tab.defaultbrowser.DefaultBrowserScreen
import com.hdw.bookmarker.feature.settings.ui.tab.folderstyle.FolderStyleScreen
import com.hdw.bookmarker.feature.settings.ui.tab.opensource.OpenSourceLicensesScreen

@Composable
internal fun SettingsNavHost(
    state: SettingsState,
    temporaryDataSize: String,
    onBackClick: () -> Unit,
    onTemporaryDataClick: () -> Unit,
    onAppUpdateClick: () -> Unit,
    onThemeModeSelect: (String) -> Unit,
    onDefaultBrowserSelect: (String) -> Unit,
    onFolderShapeSelect: (BookmarkFolderIconShape) -> Unit,
    onFolderColorSelect: (BookmarkFolderIconColor) -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SettingsNavRoute.Main,
    ) {
        slideComposable<SettingsNavRoute.Main> {
            SettingsScreen(
                onBackClick = onBackClick,
                appVersion = state.appVersion,
                temporaryDataSize = temporaryDataSize,
                selectedThemeMode = state.selectedThemeMode,
                selectedBrowserName =
                state.installedBrowsers.firstOrNull { it.packageName == state.selectedBrowserPackage }?.appName
                    ?: stringResource(R.string.bookmarker_not_selected),
                selectedBrowserIcon = state.installedBrowsers.firstOrNull {
                    it.packageName == state.selectedBrowserPackage
                }?.icon,
                onTemporaryDataClick = onTemporaryDataClick,
                onDefaultBrowserClick = {
                    navController.navigate(SettingsNavRoute.DefaultBrowser)
                },
                folderIconStyle = state.folderIconStyle,
                onFolderStyleClick = {
                    navController.navigate(SettingsNavRoute.FolderStyle)
                },
                onOpenSourceLicensesClick = {
                    navController.navigate(SettingsNavRoute.OpenSourceLicenses)
                },
                appUpdateUiState = state.appUpdateUiState,
                onAppUpdateClick = onAppUpdateClick,
                onThemeModeSelect = onThemeModeSelect,
            )
        }

        slideComposable<SettingsNavRoute.DefaultBrowser> {
            DefaultBrowserScreen(
                installedBrowsers = state.installedBrowsers,
                selectedBrowserPackage = state.selectedBrowserPackage,
                onBackClick = { navController.popBackStack() },
                onBrowserSelect = onDefaultBrowserSelect,
            )
        }

        slideComposable<SettingsNavRoute.FolderStyle> {
            FolderStyleScreen(
                selectedFolderIconStyle = state.folderIconStyle,
                onBackClick = { navController.popBackStack() },
                onShapeSelect = onFolderShapeSelect,
                onColorSelect = onFolderColorSelect,
            )
        }

        slideComposable<SettingsNavRoute.OpenSourceLicenses> {
            OpenSourceLicensesScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsNavHostPreview() {
    SettingsNavHost(
        state = SettingsState(
            appVersion = "1.2.3 (123)",
            selectedThemeMode = SettingsRepository.APP_THEME_MODE_LIGHT,
            folderIconStyle = BookmarkFolderIconStyle(
                shape = BookmarkFolderIconShape.FILLED,
                color = BookmarkFolderIconColor.DEFAULT,
            ),
            appUpdateUiState = AppUpdateUiState.UpToDate,
        ),
        temporaryDataSize = "12.3 MB",
        onBackClick = {},
        onTemporaryDataClick = {},
        onAppUpdateClick = {},
        onThemeModeSelect = {},
        onDefaultBrowserSelect = {},
        onFolderShapeSelect = {},
        onFolderColorSelect = {},
    )
}
