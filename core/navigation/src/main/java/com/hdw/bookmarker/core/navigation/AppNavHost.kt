package com.hdw.bookmarker.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hdw.bookmarker.feature.home.HomeRoute
import com.hdw.bookmarker.feature.home.guide.BrowserPickerRoute
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
                onOpenBookmark = { url, preferredBrowserPackage ->
                    ExternalAppNavigator.openBookmarkUrl(
                        context = context,
                        url = url,
                        preferredBrowserPackage = preferredBrowserPackage,
                    )
                },
                onOpenBookmarkImportGuide = {
                    navController.navigate(Route.BookmarkImportGuide)
                },
            )
        }
        slideComposable<Route.BookmarkImportGuide> {
            BrowserPickerRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onOpenDesktopGuide = { browser, selectedBrowserPackage ->
                    ExternalAppNavigator.openBrowserBookmarkGuide(
                        context = context,
                        browser = browser,
                        preferredBrowserPackage = selectedBrowserPackage,
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
