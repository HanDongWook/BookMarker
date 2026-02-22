package com.hdw.bookmarker.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hdw.bookmarker.feature.home.HomeRoute
import com.hdw.bookmarker.feature.settingsetting.SettingsRoute
import com.hdw.bookmarker.feature.settingsetting.defaultbrowser.DefaultBrowserRoute

@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = Route.Home,
    ) {
        slideComposable<Route.Home> {
            HomeRoute(
                onSettingsClick = {
                    navController.navigate(Route.Settings)
                },
                onOpenDesktopGuide = {
                    ExternalAppNavigator.openDesktopChromeBookmarkGuide(context)
                },
                onOpenBookmark = { url ->
                    ExternalAppNavigator.openBookmarkUrl(context, url)
                },
            )
        }
        slideComposable<Route.Settings> {
            SettingsRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onDefaultBrowserClick = {
                    navController.navigate(Route.SettingsDefaultBrowser)
                },
            )
        }
        slideComposable<Route.SettingsDefaultBrowser> {
            DefaultBrowserRoute(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}
