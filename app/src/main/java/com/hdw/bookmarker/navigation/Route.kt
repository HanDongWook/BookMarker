package com.hdw.bookmarker.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Main : Route

    @Serializable
    data object Settings : Route
}
