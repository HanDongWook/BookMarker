package com.hdw.bookmarker.feature.settings.navigation.appearance

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.hdw.bookmarker.core.ui.navigation.slideComposable
import com.hdw.bookmarker.feature.settings.navigation.SettingsNavRoute
import com.hdw.bookmarker.feature.settings.ui.tab.appearance.AppearanceScreen
import com.hdw.bookmarker.feature.settings.ui.tab.bookmark.BookmarkAppearanceScreen
import com.hdw.bookmarker.feature.settings.ui.tab.folder.FolderAppearanceScreen
import com.hdw.bookmarker.feature.settings.ui.tab.largescreen.LargeScreenAppearanceScreen

internal fun NavGraphBuilder.appearanceGraph(navController: NavHostController) {
    navigation<SettingsNavRoute.AppearanceGraph>(
        startDestination = SettingsNavRoute.Appearance.Main,
    ) {
        slideComposable<SettingsNavRoute.Appearance.Main> { entry ->
            AppearanceGraphRoute(
                navController = navController,
                currentEntry = entry,
            ) { appearanceState, appearanceViewModel ->
                AppearanceScreen(
                    selectedThemeMode = appearanceState.selectedThemeMode,
                    folderIconStyle = appearanceState.folderIconStyle,
                    onBackClick = { navController.popBackStack() },
                    onThemeModeSelect = appearanceViewModel::selectAppThemeMode,
                    onLargeScreenClick = {
                        navController.navigate(SettingsNavRoute.Appearance.LargeScreen)
                    },
                    onFolderClick = {
                        navController.navigate(SettingsNavRoute.Appearance.Folder)
                    },
                    onBookmarkClick = {
                        navController.navigate(SettingsNavRoute.Appearance.Bookmark)
                    },
                )
            }
        }

        slideComposable<SettingsNavRoute.Appearance.LargeScreen> { entry ->
            AppearanceGraphRoute(
                navController = navController,
                currentEntry = entry,
            ) { appearanceState, appearanceViewModel ->
                LargeScreenAppearanceScreen(
                    openBookmarkSidePreviewOnLargeScreen =
                    appearanceState.openBookmarkSidePreviewOnLargeScreen,
                    onOpenBookmarkSidePreviewOnLargeScreenChange =
                    appearanceViewModel::setOpenBookmarkSidePreviewOnLargeScreen,
                    onBackClick = { navController.popBackStack() },
                )
            }
        }

        slideComposable<SettingsNavRoute.Appearance.Bookmark> { entry ->
            AppearanceGraphRoute(
                navController = navController,
                currentEntry = entry,
            ) { appearanceState, appearanceViewModel ->
                BookmarkAppearanceScreen(
                    scrollLongBookmarkUrl = appearanceState.scrollLongBookmarkUrl,
                    showBookmarkUrl = appearanceState.showBookmarkUrl,
                    onScrollLongBookmarkUrlChange = appearanceViewModel::setScrollLongBookmarkUrl,
                    onShowBookmarkUrlChange = appearanceViewModel::setShowBookmarkUrl,
                    onBackClick = { navController.popBackStack() },
                )
            }
        }

        slideComposable<SettingsNavRoute.Appearance.Folder> { entry ->
            AppearanceGraphRoute(
                navController = navController,
                currentEntry = entry,
            ) { appearanceState, appearanceViewModel ->
                FolderAppearanceScreen(
                    selectedFolderIconStyle = appearanceState.folderIconStyle,
                    showFolderDescription = appearanceState.showFolderDescription,
                    scrollLongFolderDescription = appearanceState.scrollLongFolderDescription,
                    onBackClick = { navController.popBackStack() },
                    onShapeSelect = appearanceViewModel::selectFolderIconShape,
                    onColorSelect = appearanceViewModel::selectFolderIconColor,
                    onShowFolderDescriptionChange = appearanceViewModel::setShowFolderDescription,
                    onScrollLongFolderDescriptionChange = appearanceViewModel::setScrollLongFolderDescription,
                )
            }
        }
    }
}
