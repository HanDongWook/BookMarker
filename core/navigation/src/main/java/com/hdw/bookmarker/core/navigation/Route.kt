package com.hdw.bookmarker.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object BookmarkImportGuide : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object SettingsDefaultBrowser : Route
}
