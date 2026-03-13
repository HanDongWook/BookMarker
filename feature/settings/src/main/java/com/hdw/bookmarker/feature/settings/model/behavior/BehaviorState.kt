package com.hdw.bookmarker.feature.settings.model.behavior

import com.airbnb.mvrx.MavericksState

data class BehaviorState(
    val openBookmarkAdjacentOnLargeScreen: Boolean = false,
    val openBookmarkSidePreviewOnLargeScreen: Boolean = false,
) : MavericksState
