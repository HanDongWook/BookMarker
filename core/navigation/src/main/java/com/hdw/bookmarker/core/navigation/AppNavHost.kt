package com.hdw.bookmarker.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hdw.bookmarker.core.ui.navigation.slideComposable
import com.hdw.bookmarker.feature.home.HomeRoute
import com.hdw.bookmarker.feature.importguide.BookmarkImportGuideRoute
import com.hdw.bookmarker.feature.settingsetting.SettingsRoute
import com.hdw.bookmarker.feature.settingsetting.defaultbrowser.DefaultBrowserRoute

@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = AppRoute.Home,
    ) {
        slideComposable<AppRoute.Home> {
            HomeRoute(
                onSettingsClick = {
                    navController.navigate(AppRoute.Settings)
                },
                onOpenBookmark = { url, preferredBrowserPackage ->
                    ExternalAppNavigator.openBookmarkUrl(
                        context = context,
                        url = url,
                        preferredBrowserPackage = preferredBrowserPackage,
                    )
                },
                onOpenBookmarkImportGuide = {
                    navController.navigate(AppRoute.BookmarkImportGuide)
                },
            )
        }
        slideComposable<AppRoute.BookmarkImportGuide> {
            BookmarkImportGuideRoute(
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
        slideComposable<AppRoute.Settings> {
            SettingsRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onDefaultBrowserClick = {
                    navController.navigate(AppRoute.SettingsDefaultBrowser)
                },
            )
        }
        slideComposable<AppRoute.SettingsDefaultBrowser> {
            DefaultBrowserRoute(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}
