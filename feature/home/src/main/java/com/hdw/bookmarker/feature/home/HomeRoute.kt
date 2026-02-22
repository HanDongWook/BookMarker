package com.hdw.bookmarker.feature.home

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun HomeRoute(onSettingsClick: () -> Unit, onOpenDesktopGuide: () -> Boolean, onOpenBookmark: (String) -> Boolean) {
    val viewModel: HomeViewModel = hiltViewModel()
    HomeScreen(
        viewModel = viewModel,
        onSettingsClick = onSettingsClick,
        onOpenDesktopGuide = onOpenDesktopGuide,
        onOpenBookmark = onOpenBookmark,
    )
}
