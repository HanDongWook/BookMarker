package com.hdw.bookmarker.feature.settings.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.navigation.slideComposable
import com.hdw.bookmarker.feature.settings.behavior.BehaviorViewModel
import com.hdw.bookmarker.feature.settings.model.DisplayValueState
import com.hdw.bookmarker.feature.settings.model.SettingsState
import com.hdw.bookmarker.feature.settings.navigation.appearance.appearanceGraph
import com.hdw.bookmarker.feature.settings.ui.SettingsScreen
import com.hdw.bookmarker.feature.settings.ui.tab.appversion.AppUpdateUiState
import com.hdw.bookmarker.feature.settings.ui.tab.behavior.BehaviorScreen
import com.hdw.bookmarker.feature.settings.ui.tab.defaultbrowser.DefaultBrowserScreen
import com.hdw.bookmarker.feature.settings.ui.tab.opensource.OpenSourceLicensesScreen

@Composable
internal fun SettingsNavHost(
    settingsState: SettingsState,
    temporaryDataSize: DisplayValueState,
    onBackClick: () -> Unit,
    onTemporaryDataClick: () -> Unit,
    onAppUpdateClick: () -> Unit,
    onDefaultBrowserSelect: (String) -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SettingsNavRoute.Main,
    ) {
        slideComposable<SettingsNavRoute.Main> {
            SettingsScreen(
                onBackClick = onBackClick,
                appVersion = settingsState.appVersion,
                temporaryDataSize = temporaryDataSize,
                selectedBrowserName =
                settingsState.installedBrowsers.firstOrNull {
                    it.packageName == settingsState.selectedBrowserPackage
                }?.appName
                    ?: stringResource(R.string.bookmarker_not_selected),
                selectedBrowserIcon = settingsState.installedBrowsers.firstOrNull {
                    it.packageName == settingsState.selectedBrowserPackage
                }?.icon,
                onTemporaryDataClick = onTemporaryDataClick,
                onDefaultBrowserClick = {
                    navController.navigate(SettingsNavRoute.DefaultBrowser)
                },
                onAppearanceClick = {
                    navController.navigate(SettingsNavRoute.AppearanceGraph)
                },
                onBehaviorClick = {
                    navController.navigate(SettingsNavRoute.Behavior)
                },
                onOpenSourceLicensesClick = {
                    navController.navigate(SettingsNavRoute.OpenSourceLicenses)
                },
                appUpdateUiState = settingsState.appUpdateUiState,
                onAppUpdateClick = onAppUpdateClick,
            )
        }

        appearanceGraph(navController = navController)

        slideComposable<SettingsNavRoute.Behavior> {
            val behaviorViewModel: BehaviorViewModel = mavericksViewModel()
            val behaviorState by behaviorViewModel.collectAsState()
            BehaviorScreen(
                openBookmarkAdjacentOnLargeScreen = behaviorState.openBookmarkAdjacentOnLargeScreen,
                openBookmarkSidePreviewOnLargeScreen = behaviorState.openBookmarkSidePreviewOnLargeScreen,
                onOpenBookmarkAdjacentOnLargeScreenChange =
                    behaviorViewModel::setOpenBookmarkAdjacentOnLargeScreen,
                onOpenBookmarkSidePreviewOnLargeScreenChange =
                    behaviorViewModel::setOpenBookmarkSidePreviewOnLargeScreen,
                onBackClick = { navController.popBackStack() },
            )
        }

        slideComposable<SettingsNavRoute.DefaultBrowser> {
            DefaultBrowserScreen(
                installedBrowsers = settingsState.installedBrowsers,
                selectedBrowserPackage = settingsState.selectedBrowserPackage,
                onBackClick = { navController.popBackStack() },
                onBrowserSelect = onDefaultBrowserSelect,
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
        settingsState = SettingsState(
            appVersion = DisplayValueState.Loaded("1.2.3 (123)"),
            appUpdateUiState = AppUpdateUiState.UpToDate,
        ),
        temporaryDataSize = DisplayValueState.Loaded("12.3 MB"),
        onBackClick = {},
        onTemporaryDataClick = {},
        onAppUpdateClick = {},
        onDefaultBrowserSelect = {},
    )
}
