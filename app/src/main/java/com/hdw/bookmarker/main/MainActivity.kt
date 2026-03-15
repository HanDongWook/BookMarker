package com.hdw.bookmarker.main

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.PatternsCompat
import androidx.navigation.compose.rememberNavController
import com.hdw.bookmarker.BuildConfig
import com.hdw.bookmarker.base.BaseActivity
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerTheme
import com.hdw.bookmarker.core.navigation.AppNavHost
import com.hdw.bookmarker.feature.home.domain.model.QuickSaveBookmarkSeed
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private companion object {
        const val SPLASH_DURATION_MILLIS = 500L
    }

    private val pendingQuickSaveRequest = mutableStateOf<QuickSaveBookmarkSeed?>(null)
    private val pendingQuickSaveRequestToken = mutableStateOf<Long?>(null)

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashStartTime = SystemClock.uptimeMillis()
        installSplashScreen().setKeepOnScreenCondition {
            SystemClock.uptimeMillis() - splashStartTime < SPLASH_DURATION_MILLIS
        }

        super.onCreate(savedInstanceState)
        consumeQuickSaveIntent(intent)
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
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        consumeQuickSaveIntent(intent)
    }

    private fun consumeQuickSaveIntent(intent: Intent?) {
        val quickSaveRequest = parseQuickSaveRequest(intent) ?: return
        pendingQuickSaveRequest.value = quickSaveRequest
        pendingQuickSaveRequestToken.value = SystemClock.uptimeMillis()
    }

    private fun parseQuickSaveRequest(intent: Intent?): QuickSaveBookmarkSeed? {
        if (intent?.action != Intent.ACTION_SEND) return null
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty().trim()
        val extractedUrl = extractFirstUrl(sharedText) ?: return null
        val rawTitle = intent.getStringExtra(Intent.EXTRA_TITLE)
            ?: intent.getStringExtra(Intent.EXTRA_SUBJECT)
            ?: extractedUrl
        val cleanedDescription = sharedText
            .replace(extractedUrl, "")
            .trim()
            .takeIf { it.isNotBlank() }
            .orEmpty()
        return QuickSaveBookmarkSeed(
            title = rawTitle.trim().ifBlank { extractedUrl },
            url = extractedUrl,
            description = cleanedDescription,
        )
    }

    private fun extractFirstUrl(text: String): String? {
        if (text.isBlank()) return null
        val matcher = PatternsCompat.WEB_URL.matcher(text)
        while (matcher.find()) {
            val candidate = matcher.group().trim()
            if (candidate.startsWith("http://") || candidate.startsWith("https://")) {
                return candidate
            }
        }
        return null
    }
}
