package com.hdw.bookmarker.main

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.hdw.bookmarker.BuildConfig
import com.hdw.bookmarker.base.BaseActivity
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerTheme
import com.hdw.bookmarker.core.navigation.AppNavHost
import com.hdw.bookmarker.core.navigation.AppRoute
import com.hdw.bookmarker.feature.home.domain.model.QuickSaveBookmarkSeed
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    companion object {
        const val SPLASH_DURATION_MILLIS = 500L
    }

    private val pendingQuickSaveRequest = mutableStateOf<QuickSaveBookmarkSeed?>(null)
    private val pendingQuickSaveRequestToken = mutableStateOf<Long?>(null)
    private val pendingDeepLinkTarget = mutableStateOf<AppNavigationDeepLink.Target?>(null)
    private val pendingDeepLinkToken = mutableStateOf<Long?>(null)
    private val quickSaveIntentParser = QuickSaveIntentParser()
    private val navigationDeepLinkIntentParser = NavigationDeepLinkIntentParser()

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashStartTime = SystemClock.uptimeMillis()
        installSplashScreen().setKeepOnScreenCondition {
            SystemClock.uptimeMillis() - splashStartTime < SPLASH_DURATION_MILLIS
        }

        super.onCreate(savedInstanceState)
        consumeQuickSaveIntent(intent)
        consumeNavigationDeepLinkIntent(intent)
        setContent {
            val appThemeMode = settingsRepository.getAppThemeModeFlow().collectAsState(initial = null).value
            val isDarkTheme = when (appThemeMode) {
                SettingsRepository.APP_THEME_MODE_DARK -> true
                SettingsRepository.APP_THEME_MODE_LIGHT -> false
                else -> isSystemInDarkTheme()
            }
            BookMarkerTheme(
                darkTheme = isDarkTheme,
            ) {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    isDebugBuild = BuildConfig.DEBUG,
                    pendingQuickSaveRequestToken = pendingQuickSaveRequestToken.value,
                    pendingQuickSaveRequest = pendingQuickSaveRequest.value,
                    onQuickSaveRequestHandled = {
                        pendingQuickSaveRequest.value = null
                        pendingQuickSaveRequestToken.value = null
                    },
                )

                LaunchedEffect(pendingDeepLinkToken.value) {
                    val target = pendingDeepLinkTarget.value ?: return@LaunchedEffect
                    when (target) {
                        AppNavigationDeepLink.Target.Home -> {
                            navController.popBackStack(
                                route = AppRoute.Home,
                                inclusive = false,
                            )
                        }

                        AppNavigationDeepLink.Target.Settings -> {
                            navController.navigate(AppRoute.Settings) { launchSingleTop = true }
                        }

                        AppNavigationDeepLink.Target.ImportGuide -> {
                            navController.navigate(AppRoute.BookmarkImportGuide) { launchSingleTop = true }
                        }
                    }
                    pendingDeepLinkTarget.value = null
                    pendingDeepLinkToken.value = null
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        consumeQuickSaveIntent(intent)
        consumeNavigationDeepLinkIntent(intent)
    }

    private fun consumeQuickSaveIntent(intent: Intent?) {
        val quickSaveRequest = quickSaveIntentParser.parse(intent) ?: return
        pendingQuickSaveRequest.value = quickSaveRequest
        pendingQuickSaveRequestToken.value = SystemClock.uptimeMillis()
    }

    private fun consumeNavigationDeepLinkIntent(intent: Intent?) {
        val target = navigationDeepLinkIntentParser.parse(intent) ?: return
        pendingDeepLinkTarget.value = target
        pendingDeepLinkToken.value = SystemClock.uptimeMillis()
    }
}
