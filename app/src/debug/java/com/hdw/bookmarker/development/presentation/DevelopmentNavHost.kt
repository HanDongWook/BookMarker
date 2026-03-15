package com.hdw.bookmarker.development.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hdw.bookmarker.core.ui.navigation.slideComposable
import com.hdw.bookmarker.development.navigation.DevelopmentRoute
import com.hdw.bookmarker.development.presentation.component.DevelopmentTopAppBar
import com.hdw.bookmarker.development.presentation.tab.deeplink.DeepLinkScreen

@Composable
internal fun DevelopmentNavHost(
    onBackClick: () -> Unit,
    onOpenShowkaseClick: () -> Unit,
    onInjectMockDataClick: () -> Unit,
    onRunPerformanceBenchmarkClick: () -> Unit,
    onDeepLinkHomeClick: () -> Unit,
    onDeepLinkSettingsClick: () -> Unit,
    onDeepLinkImportGuideClick: () -> Unit,
    performanceSummary: String?,
    onPerformanceSummaryDismiss: () -> Unit,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val isDeepLinkScreen = backStackEntry?.destination?.hasRoute<DevelopmentRoute.DeepLink>() == true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            DevelopmentTopAppBar(
                isDeepLinkScreen = isDeepLinkScreen,
                onBackClick = {
                    if (isDeepLinkScreen) {
                        navController.popBackStack()
                    } else {
                        onBackClick()
                    }
                },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DevelopmentRoute.Main,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            slideComposable<DevelopmentRoute.Main> {
                DevelopmentScreen(
                    onOpenDeepLinkScreenClick = {
                        navController.navigate(DevelopmentRoute.DeepLink)
                    },
                    onOpenShowkaseClick = onOpenShowkaseClick,
                    onInjectMockDataClick = onInjectMockDataClick,
                    onRunPerformanceBenchmarkClick = onRunPerformanceBenchmarkClick,
                    performanceSummary = performanceSummary,
                    onPerformanceSummaryDismiss = onPerformanceSummaryDismiss,
                )
            }
            slideComposable<DevelopmentRoute.DeepLink> {
                DeepLinkScreen(
                    onDeepLinkHomeClick = onDeepLinkHomeClick,
                    onDeepLinkSettingsClick = onDeepLinkSettingsClick,
                    onDeepLinkImportGuideClick = onDeepLinkImportGuideClick,
                )
            }
        }
    }
}
