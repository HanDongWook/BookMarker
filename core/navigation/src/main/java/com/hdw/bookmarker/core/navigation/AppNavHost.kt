package com.hdw.bookmarker.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hdw.bookmarker.core.ui.navigation.slideComposable
import com.hdw.bookmarker.feature.home.domain.model.QuickSaveBookmarkSeed
import com.hdw.bookmarker.feature.home.presentation.HomeRoute
import com.hdw.bookmarker.feature.importguide.presentation.BookmarkImportGuideRoute
import com.hdw.bookmarker.feature.settings.presentation.SettingsRoute
import com.hdw.bookmarker.feature.trends.presentation.TrendsRoute

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
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = currentDestination?.hasRoute<AppRoute.Bookmarks>() == true ||
        currentDestination?.hasRoute<AppRoute.Trends>() == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    currentDestination = currentDestination,
                    onBookmarksClick = {
                        navController.navigate(AppRoute.Bookmarks) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onTrendsClick = {
                        navController.navigate(AppRoute.Trends) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Bookmarks,
            modifier = Modifier.padding(innerPadding),
        ) {
            slideComposable<AppRoute.Bookmarks> { entry ->
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
            slideComposable<AppRoute.Trends> {
                TrendsRoute()
            }
            slideComposable<AppRoute.BookmarkImportGuide> {
                BookmarkImportGuideRoute(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onImportHtmlFile = {
                        navController.getBackStackEntry(AppRoute.Bookmarks)
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
}
