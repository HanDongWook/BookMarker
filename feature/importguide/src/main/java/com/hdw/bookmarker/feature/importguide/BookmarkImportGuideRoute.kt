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
import com.hdw.bookmarker.feature.importguide.model.BrowserGuideCatalog
import com.hdw.bookmarker.feature.importguide.route.FeatureBookmarkImportGuideRoute
import com.hdw.bookmarker.feature.importguide.ui.detail.BookmarkImportGuideScreen
import com.hdw.bookmarker.feature.importguide.ui.picker.BrowserPickerScreen
import com.hdw.bookmarker.feature.importguide.ui.root.ImportBookmarksRootScreen
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BookmarkImportGuideRoute(
    onBackClick: () -> Unit,
    onImportHtmlFile: () -> Unit,
    onOpenDesktopGuide: (Browser, String?) -> Boolean,
) {
    val viewModel: BookmarkImportGuideViewModel = hiltViewModel()
    val navController: NavHostController = rememberNavController()
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val resources = LocalResources.current

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = FeatureBookmarkImportGuideRoute.Root,
        ) {
            slideComposable<FeatureBookmarkImportGuideRoute.Root> {
                ImportBookmarksRootScreen(
                    onImportHtmlFileClick = onImportHtmlFile,
                    onOpenBrowserGuidesClick = {
                        navController.navigate(FeatureBookmarkImportGuideRoute.BrowserGuides)
                    },
                    onBackClick = onBackClick,
                )
            }

            slideComposable<FeatureBookmarkImportGuideRoute.BrowserGuides> {
                BrowserPickerScreen(
                    guideItems = state.guideItems,
                    onOpenDesktopGuide = { browser ->
                        navController.navigate(
                            FeatureBookmarkImportGuideRoute.BrowserGuideDetail(browser.name),
                        )
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    iconModifierForBrowser = { guideItem ->
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = "browser-icon-${guideItem.browser.name}",
                            ),
                            animatedVisibilityScope = this,
                        )
                    },
                    textModifierForBrowser = { guideItem ->
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = "browser-name-${guideItem.browser.name}",
                            ),
                            animatedVisibilityScope = this,
                        )
                    },
                )
            }

            slideComposable<FeatureBookmarkImportGuideRoute.BrowserGuideDetail> { entry ->
                BackHandler {
                    navController.popBackStack()
                }

                val defaultGuideItem = BrowserGuideCatalog.defaultGuideItem()
                val selectedBrowser = runCatching {
                    Browser.valueOf(entry.toRoute<FeatureBookmarkImportGuideRoute.BrowserGuideDetail>().browserId)
                }.getOrElse { defaultGuideItem.browser }
                val currentSelectedGuideItem = state.guideItems
                    .firstOrNull { it.browser == selectedBrowser }
                    ?: BrowserGuideCatalog.findByBrowser(selectedBrowser)
                    ?: defaultGuideItem

                BookmarkImportGuideScreen(
                    icon = currentSelectedGuideItem.installedBrowser?.icon,
                    browser = currentSelectedGuideItem.browser,
                    browserName = currentSelectedGuideItem.displayName,
                    showGuideButton = currentSelectedGuideItem.hasGuideLink,
                    onDismiss = {
                        navController.popBackStack()
                    },
                    onOpenDesktopGuide = {
                        if (
                            !onOpenDesktopGuide(
                                currentSelectedGuideItem.browser,
                                currentSelectedGuideItem.installedBrowser?.packageName,
                            )
                        ) {
                            context.showShortToast(resources.getString(R.string.import_guide_open_guide_failed))
                        }
                    },
                    iconModifier = currentSelectedGuideItem.let { guideItem ->
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = "browser-icon-${guideItem.browser.name}",
                            ),
                            animatedVisibilityScope = this,
                        )
                    },
                    browserNameModifier = currentSelectedGuideItem.let { guideItem ->
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(
                                key = "browser-name-${guideItem.browser.name}",
                            ),
                            animatedVisibilityScope = this,
                        )
                    },
                )
            }
        }
    }
}
