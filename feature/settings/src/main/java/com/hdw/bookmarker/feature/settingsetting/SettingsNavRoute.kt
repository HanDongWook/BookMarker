package com.hdw.bookmarker.feature.settingsetting

import kotlinx.serialization.Serializable

sealed interface SettingsNavRoute {
    @Serializable
    data object Main : SettingsNavRoute

    @Serializable
    data object DefaultBrowser : SettingsNavRoute

    @Serializable
    data object FolderStyle : SettingsNavRoute
}
