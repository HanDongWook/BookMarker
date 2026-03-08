package com.hdw.bookmarker.feature.importguide

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.navigation.slideComposable
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.importguide.route.FeatureBookmarkImportGuideRoute
import com.hdw.bookmarker.feature.importguide.ui.detail.BookmarkImportGuideScreen
import com.hdw.bookmarker.feature.importguide.ui.picker.BrowserPickerScreen
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BookmarkImportGuideRoute(onBackClick: () -> Unit, onOpenDesktopGuide: (Browser, String?) -> Boolean) {
    val viewModel: BookmarkImportGuideViewModel = hiltViewModel()
    val navController: NavHostController = rememberNavController()
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val resources = LocalResources.current

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = FeatureBookmarkImportGuideRoute.Picker,
        ) {
            slideComposable<FeatureBookmarkImportGuideRoute.Picker> {
                BrowserPickerScreen(
                    installedBrowsers = state.installedBrowsers,
                    onOpenDesktopGuide = { packageName ->
                        viewModel.onBrowserSelected(packageName)
                        navController.navigate(FeatureBookmarkImportGuideRoute.GuideFeatureBookmark(packageName))
                    },
                    onBackClick = onBackClick,
                    iconModifierForBrowser = { browser ->
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = "browser-icon-${browser.packageName}",
                            ),
                            animatedVisibilityScope = this,
                        )
                    },
                    textModifierForBrowser = { browser ->
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = "browser-name-${browser.packageName}",
                            ),
                            animatedVisibilityScope = this,
                        )
                    },
                )
            }

            slideComposable<FeatureBookmarkImportGuideRoute.GuideFeatureBookmark> { entry ->
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
                            sharedContentState = rememberSharedContentState(
                                key = "browser-icon-${browser.packageName}",
                            ),
                            animatedVisibilityScope = this,
                        )
                    } ?: Modifier,
                    browserNameModifier = currentSelectedBrowser?.let { browser ->
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = "browser-name-${browser.packageName}",
                            ),
                            animatedVisibilityScope = this,
                        )
                    } ?: Modifier,
                )
            }
        }
    }
}
