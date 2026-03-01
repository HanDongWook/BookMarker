package com.hdw.bookmarker.feature.importguide

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.importguide.detail.BookmarkImportGuideScreen
import com.hdw.bookmarker.feature.importguide.picker.BrowserPickerScreen
import com.hdw.bookmarker.feature.importguide.picker.BrowserPickerViewModel
import com.hdw.bookmarker.feature.importguide.route.FeatureBookmarkImportGuideRoute
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BrowserPickerRoute(onBackClick: () -> Unit, onOpenDesktopGuide: (Browser, String?) -> Boolean) {
    val viewModel: BrowserPickerViewModel = hiltViewModel()
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val resources = LocalResources.current
    val navController = rememberNavController()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = FeatureBookmarkImportGuideRoute.Picker,
        ) {
            composable<FeatureBookmarkImportGuideRoute.Picker>(
                enterTransition = { EnterTransition.None },
                exitTransition = {
                    slideOutHorizontally(
                        animationSpec = tween(durationMillis = 320),
                        targetOffsetX = { -it / 4 },
                    )
                },
                popEnterTransition = {
                    slideInHorizontally(
                        animationSpec = tween(durationMillis = 320),
                        initialOffsetX = { -it / 4 },
                    )
                },
                popExitTransition = { ExitTransition.None },
            ) {
                BrowserPickerScreen(
                    installedBrowsers = state.installedBrowsers,
                    onOpenDesktopGuide = { packageName ->
                        viewModel.onBrowserSelected(packageName)
                        navController.navigate(FeatureBookmarkImportGuideRoute.GuideFeatureBookmark(packageName))
                    },
                    onBackClick = onBackClick,
                    iconModifierForBrowser = { browser ->
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(key = "browser-icon-${browser.packageName}"),
                            animatedVisibilityScope = this@composable,
                        )
                    },
                )
            }

            composable<FeatureBookmarkImportGuideRoute.GuideFeatureBookmark>(
                enterTransition = {
                    slideInHorizontally(
                        animationSpec = tween(durationMillis = 320),
                        initialOffsetX = { it / 4 },
                    )
                },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = {
                    slideOutHorizontally(
                        animationSpec = tween(durationMillis = 320),
                        targetOffsetX = { it / 4 },
                    )
                },
            ) { entry ->
                BackHandler {
                    viewModel.clearSelectedBrowser()
                    navController.popBackStack()
                }

                val selectedPackage = entry.toRoute<FeatureBookmarkImportGuideRoute.GuideFeatureBookmark>().packageName
                val currentSelectedBrowser = state.installedBrowsers
                    .find { it.packageName == selectedPackage }
                    ?: state.currentSelectedBrowser

                BookmarkImportGuideScreen(
                    icon = currentSelectedBrowser?.icon,
                    browserPackageName = currentSelectedBrowser?.packageName,
                    browserName = currentSelectedBrowser?.appName,
                    onDismiss = {
                        viewModel.clearSelectedBrowser()
                        navController.popBackStack()
                    },
                    onOpenDesktopGuide = {
                        val selectedBrowserType = Browser.fromPackageAndName(
                            packageName = currentSelectedBrowser?.packageName,
                            appName = currentSelectedBrowser?.appName,
                        )
                        if (!onOpenDesktopGuide(selectedBrowserType, currentSelectedBrowser?.packageName)) {
                            context.showShortToast(resources.getString(R.string.import_guide_open_guide_failed))
                        }
                    },
                    iconModifier = currentSelectedBrowser?.let { browser ->
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(key = "browser-icon-${browser.packageName}"),
                            animatedVisibilityScope = this@composable,
                        )
                    } ?: Modifier,
                )
            }
        }
    }
}
