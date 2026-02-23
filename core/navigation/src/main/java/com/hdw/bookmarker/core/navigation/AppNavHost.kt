package com.hdw.bookmarker.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.feature.home.HomeRoute
import com.hdw.bookmarker.feature.settingsetting.SettingsRoute
import com.hdw.bookmarker.feature.settingsetting.defaultbrowser.DefaultBrowserRoute

@Composable
fun AppNavHost(navController: NavHostController, defaultBrowserPackage: String?) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = Route.Home,
    ) {
        slideComposable<Route.Home> {
            HomeRoute(
                defaultBrowserPackage = defaultBrowserPackage,
                onSettingsClick = {
                    navController.navigate(Route.Settings)
                },
                onOpenDesktopGuide = { browser, selectedBrowserPackage ->
                    ExternalAppNavigator.openBrowserBookmarkGuide(
                        context = context,
                        browser = browser,
                        preferredBrowserPackage = selectedBrowserPackage,
                    )
                },
                onOpenBookmark = { url ->
                    ExternalAppNavigator.openBookmarkUrl(
                        context = context,
                        url = url,
                        preferredBrowserPackage = defaultBrowserPackage,
                    )
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
