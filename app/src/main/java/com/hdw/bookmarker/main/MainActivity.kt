package com.hdw.bookmarker.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.hdw.bookmarker.base.BaseActivity
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerTheme
import com.hdw.bookmarker.core.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookMarkerTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                val defaultBrowserPackage by settingsRepository
                    .getDefaultBrowserPackageFlow()
                    .collectAsState(initial = null)
                AppNavHost(
                    navController = navController,
                    defaultBrowserPackage = defaultBrowserPackage,
                    onDefaultBrowserSelected = { packageName ->
                        scope.launch {
                            settingsRepository.setDefaultBrowserPackage(packageName)
                        }
                    },
                )
            }
        }
    }
}
