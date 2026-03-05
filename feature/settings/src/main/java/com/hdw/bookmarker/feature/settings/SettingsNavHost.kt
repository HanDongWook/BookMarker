package com.hdw.bookmarker.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.core.ui.navigation.slideComposable
import com.hdw.bookmarker.feature.settings.defaultbrowser.DefaultBrowserScreen
import com.hdw.bookmarker.feature.settings.folderstyle.FolderStyleScreen
import com.hdw.bookmarker.feature.settings.opensource.OpenSourceLicensesScreen

@Composable
internal fun SettingsNavHost(
    state: SettingsState,
    temporaryDataSize: String,
    onBackClick: () -> Unit,
    onTemporaryDataClick: () -> Unit,
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
                selectedFolderIconShape = state.selectedFolderIconShape,
                selectedFolderIconColor = state.selectedFolderIconColor,
                onFolderStyleClick = {
                    navController.navigate(SettingsNavRoute.FolderStyle)
                },
                onOpenSourceLicensesClick = {
                    navController.navigate(SettingsNavRoute.OpenSourceLicenses)
                },
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
                selectedShape = state.selectedFolderIconShape,
                selectedColor = state.selectedFolderIconColor,
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
