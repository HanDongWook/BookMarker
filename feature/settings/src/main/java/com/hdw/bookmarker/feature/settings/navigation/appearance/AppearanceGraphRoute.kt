package com.hdw.bookmarker.feature.settings.navigation.appearance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.hdw.bookmarker.feature.settings.appearance.AppearanceViewModel
import com.hdw.bookmarker.feature.settings.model.appearance.AppearanceState

@Composable
internal fun AppearanceGraphRoute(
    navController: NavHostController,
    currentEntry: NavBackStackEntry,
    content: @Composable (AppearanceState, AppearanceViewModel) -> Unit,
) {
    val appearanceGraphEntry = remember(currentEntry, navController) {
        val parentRoute = currentEntry.destination.parent?.route
            ?: error("Appearance destination must have a parent graph route")
        navController.getBackStackEntry(parentRoute)
    }
    val appearanceViewModel: AppearanceViewModel = mavericksViewModel(scope = appearanceGraphEntry)
    val appearanceState by appearanceViewModel.collectAsState()

    content(appearanceState, appearanceViewModel)
}
