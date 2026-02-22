package com.hdw.bookmarker.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.hdw.bookmarker.feature.home.HomeScreen
import com.hdw.bookmarker.feature.home.HomeViewModel
import com.hdw.bookmarker.feature.settingsetting.SettingsScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = Route.Home,
    ) {
        slideComposable<Route.Home> {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onSettingsClick = {
                    navController.navigate(Route.Settings)
                },
                onOpenDesktopGuide = {
                    ExternalAppNavigator.openDesktopChromeBookmarkGuide(context)
                },
            )
        }
        slideComposable<Route.Settings> {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
            )
        }
    }
}
