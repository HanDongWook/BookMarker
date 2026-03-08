package com.hdw.bookmarker.feature.settings.navigation
import kotlinx.serialization.Serializable

sealed interface SettingsNavRoute {

    @Serializable
    data object Main : SettingsNavRoute

    @Serializable
    data object DefaultBrowser : SettingsNavRoute

    @Serializable
    data object FolderStyle : SettingsNavRoute

    @Serializable
    data object OpenSourceLicenses : SettingsNavRoute
}
