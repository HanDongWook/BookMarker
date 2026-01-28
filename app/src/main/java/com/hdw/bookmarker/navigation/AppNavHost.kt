package com.hdw.bookmarker.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hdw.bookmarker.main.MainScreen
import com.hdw.bookmarker.main.MainViewModel
import com.hdw.bookmarker.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.Main
    ) {
        slideComposable<Route.Main> {
            val viewModel: MainViewModel = hiltViewModel()
            MainScreen(
                installedBrowsers = viewModel.getInstalledBrowsers(),
                onSettingsClick = {
                    navController.navigate(Route.Settings)
                },
                onSyncClick = viewModel::onSync
            )
        }
        slideComposable<Route.Settings> {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
