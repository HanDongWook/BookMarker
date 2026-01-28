package com.hdw.bookmarker.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.hdw.bookmarker.base.BaseActivity
import com.hdw.bookmarker.navigation.BookMarkerNavHost
import com.hdw.bookmarker.ui.theme.BookMarkerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookMarkerTheme {
                val navController = rememberNavController()
                BookMarkerNavHost(
                    navController = navController,
                    installedBrowsers = viewModel.getInstalledBrowsers()
                )
            }
        }
    }
}
