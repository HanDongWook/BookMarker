package com.hdw.bookmarker.core.navigation

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object Bookmarks : AppRoute

    @Serializable
    data object Trends : AppRoute

    @Serializable
    data object BookmarkImportGuide : AppRoute

    @Serializable
    data object Settings : AppRoute
}
