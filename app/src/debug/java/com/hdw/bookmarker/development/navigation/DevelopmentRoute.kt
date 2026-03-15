package com.hdw.bookmarker.development.navigation

import kotlinx.serialization.Serializable

internal sealed interface DevelopmentRoute {
    @Serializable
    data object Main : DevelopmentRoute

    @Serializable
    data object DeepLink : DevelopmentRoute
}
