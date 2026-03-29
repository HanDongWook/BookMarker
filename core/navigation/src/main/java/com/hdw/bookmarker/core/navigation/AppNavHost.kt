package com.hdw.bookmarker.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hdw.bookmarker.core.ui.navigation.slideComposable
import com.hdw.bookmarker.feature.home.domain.model.QuickSaveBookmarkSeed
import com.hdw.bookmarker.feature.home.presentation.HomeBottomBarActionState
import com.hdw.bookmarker.feature.home.presentation.HomeRoute
import com.hdw.bookmarker.feature.importguide.presentation.BookmarkImportGuideRoute
import com.hdw.bookmarker.feature.settings.presentation.SettingsRoute
import com.hdw.bookmarker.feature.trends.presentation.TrendsRoute
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

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
    var bookmarksBottomBarActionState by remember { mutableStateOf(HomeBottomBarActionState()) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isBookmarksRoute = currentDestination?.hasRoute<AppRoute.Bookmarks>() == true
    val showBottomBar = currentDestination?.hasRoute<AppRoute.Bookmarks>() == true ||
        currentDestination?.hasRoute<AppRoute.Trends>() == true
    val currentBottomBarRoute = when {
        currentDestination?.hasRoute<AppRoute.Bookmarks>() == true -> AppRoute.Bookmarks
        currentDestination?.hasRoute<AppRoute.Trends>() == true -> AppRoute.Trends
        else -> null
    }
    val backgroundColor = MaterialTheme.colorScheme.surface
    val backdrop = rememberLayerBackdrop {
        drawRect(backgroundColor)
        drawContent()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = AppRoute.Bookmarks,
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(backdrop),
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
                    onBottomBarActionStateChange = { actionState ->
                        bookmarksBottomBarActionState = actionState
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

        if (showBottomBar) {
            val onBookmarksClick = {
                navController.navigate(AppRoute.Bookmarks) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            val onTrendsClick = {
                navController.navigate(AppRoute.Trends) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            if (isBookmarksRoute && bookmarksBottomBarActionState.showAddButton) {
                BookmarksBottomBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    backdrop = backdrop,
                    currentRoute = currentBottomBarRoute,
                    onBookmarksClick = onBookmarksClick,
                    onTrendsClick = onTrendsClick,
                    onAddClick = {
                        bookmarksBottomBarActionState.onAddButtonClick?.invoke()
                    },
                )
            } else {
                AppBottomBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .height(72.dp),
                    backdrop = backdrop,
                    currentRoute = currentBottomBarRoute,
                    onBookmarksClick = onBookmarksClick,
                    onTrendsClick = onTrendsClick,
                )
            }
        }
    }
}
