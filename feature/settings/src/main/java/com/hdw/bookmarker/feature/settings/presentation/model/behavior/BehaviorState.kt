package com.hdw.bookmarker.feature.settings.presentation.model.behavior

import com.airbnb.mvrx.MavericksState

data class BehaviorState(val openBookmarkAdjacentOnLargeScreen: Boolean = false) : MavericksState
