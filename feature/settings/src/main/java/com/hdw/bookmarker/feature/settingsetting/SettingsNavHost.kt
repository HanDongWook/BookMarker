package com.hdw.bookmarker.feature.settingsetting

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.feature.settingsetting.defaultbrowser.DefaultBrowserScreen
import com.hdw.bookmarker.feature.settingsetting.folderstyle.FolderStyleScreen

@Composable
internal fun SettingsNavHost(
    state: SettingsState,
    onBackClick: () -> Unit,
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
        composable<SettingsNavRoute.Main> {
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
                onDefaultBrowserClick = {
                    navController.navigate(SettingsNavRoute.DefaultBrowser)
                },
                selectedFolderIconShape = state.selectedFolderIconShape,
                selectedFolderIconColor = state.selectedFolderIconColor,
                onFolderStyleClick = {
                    navController.navigate(SettingsNavRoute.FolderStyle)
                },
                onThemeModeSelect = onThemeModeSelect,
            )
        }

        composable<SettingsNavRoute.DefaultBrowser> {
            DefaultBrowserScreen(
                installedBrowsers = state.installedBrowsers,
                selectedBrowserPackage = state.selectedBrowserPackage,
                onBackClick = { navController.popBackStack() },
                onBrowserSelect = onDefaultBrowserSelect,
            )
        }

        composable<SettingsNavRoute.FolderStyle> {
            FolderStyleScreen(
                selectedShape = state.selectedFolderIconShape,
                selectedColor = state.selectedFolderIconColor,
                onBackClick = { navController.popBackStack() },
                onShapeSelect = onFolderShapeSelect,
                onColorSelect = onFolderColorSelect,
            )
        }
    }
}
