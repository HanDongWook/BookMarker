package com.hdw.bookmarker.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hdw.bookmarker.core.ui.navigation.slideComposable
import com.hdw.bookmarker.feature.home.HomeRoute
import com.hdw.bookmarker.feature.importguide.BookmarkImportGuideRoute
import com.hdw.bookmarker.feature.settings.SettingsRoute

@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    val importHtmlRequestKey = "import_html_request_token"
    NavHost(
        navController = navController,
        startDestination = AppRoute.Home,
    ) {
        slideComposable<AppRoute.Home> { entry ->
            HomeRoute(
                onSettingsClick = {
                    navController.navigate(AppRoute.Settings)
                },
                onOpenBookmark = { request ->
                    ExternalAppNavigator.openBookmarkUrl(
                        context = context,
                        request = request,
                    )
                },
                onOpenBookmarkImportGuide = {
                    navController.navigate(AppRoute.BookmarkImportGuide)
                },
                pendingImportHtmlRequestToken = entry.savedStateHandle.get<Long?>(importHtmlRequestKey),
                onImportHtmlRequestHandled = {
                    entry.savedStateHandle[importHtmlRequestKey] = null
                },
            )
        }
        slideComposable<AppRoute.BookmarkImportGuide> {
            BookmarkImportGuideRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onImportHtmlFile = {
                    navController.getBackStackEntry(AppRoute.Home)
                        .savedStateHandle[importHtmlRequestKey] = System.currentTimeMillis()
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
            )
        }
    }
}
