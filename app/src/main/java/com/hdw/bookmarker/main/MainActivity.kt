package com.hdw.bookmarker.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.hdw.bookmarker.base.BaseActivity
import com.hdw.bookmarker.ui.theme.BookMarkerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookMarkerTheme {
                MainScreen(
                    installedBrowsers = viewModel.getInstalledBrowsers()
                )
            }
        }
    }
}
