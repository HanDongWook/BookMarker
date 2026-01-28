package com.hdw.bookmarker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hdw.bookmarker.main.MainScreen
import com.hdw.bookmarker.model.BrowserInfo
import com.hdw.bookmarker.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    installedBrowsers: List<BrowserInfo>
) {
    NavHost(
        navController = navController,
        startDestination = Route.Main
    ) {
        slideComposable<Route.Main> {
            MainScreen(
                installedBrowsers = installedBrowsers,
                onSettingsClick = {
                    navController.navigate(Route.Settings)
                }
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
