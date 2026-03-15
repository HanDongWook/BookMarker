package com.hdw.bookmarker.feature.settings.presentation.navigation
import kotlinx.serialization.Serializable

sealed interface SettingsNavRoute {

    @Serializable
    data object Main : SettingsNavRoute

    @Serializable
    data object AppearanceGraph : SettingsNavRoute

    @Serializable
    data object Behavior : SettingsNavRoute

    @Serializable
    data object DefaultBrowser : SettingsNavRoute

    sealed interface Appearance : SettingsNavRoute {

        @Serializable
        data object Main : Appearance

        @Serializable
        data object LargeScreen : Appearance

        @Serializable
        data object Bookmark : Appearance

        @Serializable
        data object Folder : Appearance
    }

    @Serializable
    data object OpenSourceLicenses : SettingsNavRoute
}
