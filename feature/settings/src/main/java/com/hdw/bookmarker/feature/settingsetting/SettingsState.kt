package com.hdw.bookmarker.feature.settingsetting

import com.airbnb.mvrx.MavericksState

data class SettingsState(
    val appVersion: String = "-",
) : MavericksState
