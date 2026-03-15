package com.hdw.bookmarker.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hdw.bookmarker.core.ui.navigation.slideComposable
import com.hdw.bookmarker.feature.home.domain.model.QuickSaveBookmarkSeed
import com.hdw.bookmarker.feature.home.presentation.HomeRoute
import com.hdw.bookmarker.feature.importguide.presentation.BookmarkImportGuideRoute
import com.hdw.bookmarker.feature.settings.presentation.SettingsRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    isDebugBuild: Boolean = false,
    pendingQuickSaveRequestToken: Long? = null,
    pendingQuickSaveRequest: QuickSaveBookmarkSeed? = null,
    onQuickSaveRequestHandled: () -> Unit = {},
) {
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
                pendingQuickSaveRequestToken = pendingQuickSaveRequestToken,
                pendingQuickSaveRequest = pendingQuickSaveRequest,
                onQuickSaveRequestHandled = onQuickSaveRequestHandled,
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
                isDebugBuild = isDebugBuild,
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}
