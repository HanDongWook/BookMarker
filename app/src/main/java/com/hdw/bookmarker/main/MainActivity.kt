package com.hdw.bookmarker.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.rememberNavController
import com.hdw.bookmarker.base.BaseActivity
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerTheme
import com.hdw.bookmarker.core.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                )
            }
        }
    }
}
