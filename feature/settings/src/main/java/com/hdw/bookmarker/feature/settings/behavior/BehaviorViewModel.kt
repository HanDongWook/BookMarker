package com.hdw.bookmarker.feature.settings.behavior

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.hiltMavericksViewModelFactory
import com.hdw.bookmarker.core.domain.usecase.GetOpenBookmarkAdjacentOnLargeScreenUseCase
import com.hdw.bookmarker.core.domain.usecase.GetOpenBookmarkSidePreviewOnLargeScreenUseCase
import com.hdw.bookmarker.core.domain.usecase.SetOpenBookmarkAdjacentOnLargeScreenUseCase
import com.hdw.bookmarker.core.domain.usecase.SetOpenBookmarkSidePreviewOnLargeScreenUseCase
import com.hdw.bookmarker.feature.settings.model.behavior.BehaviorState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BehaviorViewModel @AssistedInject constructor(
    @Assisted initialState: BehaviorState,
    private val getOpenBookmarkAdjacentOnLargeScreenUseCase: GetOpenBookmarkAdjacentOnLargeScreenUseCase,
    private val getOpenBookmarkSidePreviewOnLargeScreenUseCase: GetOpenBookmarkSidePreviewOnLargeScreenUseCase,
    private val setOpenBookmarkAdjacentOnLargeScreenUseCase: SetOpenBookmarkAdjacentOnLargeScreenUseCase,
    private val setOpenBookmarkSidePreviewOnLargeScreenUseCase: SetOpenBookmarkSidePreviewOnLargeScreenUseCase,
) : MavericksViewModel<BehaviorState>(initialState) {
    init {
        observeBehaviorSettings()
    }

    fun setOpenBookmarkAdjacentOnLargeScreen(enabled: Boolean) {
        setState { copy(openBookmarkAdjacentOnLargeScreen = enabled) }
        viewModelScope.launch {
            setOpenBookmarkAdjacentOnLargeScreenUseCase(enabled)
        }
    }

    fun setOpenBookmarkSidePreviewOnLargeScreen(enabled: Boolean) {
        setState { copy(openBookmarkSidePreviewOnLargeScreen = enabled) }
        viewModelScope.launch {
            setOpenBookmarkSidePreviewOnLargeScreenUseCase(enabled)
        }
    }

    private fun observeBehaviorSettings() {
        viewModelScope.launch {
            getOpenBookmarkAdjacentOnLargeScreenUseCase().collectLatest { enabled ->
                withState { current ->
                    if (current.openBookmarkAdjacentOnLargeScreen == enabled) return@withState
                    setState { copy(openBookmarkAdjacentOnLargeScreen = enabled) }
                }
            }
        }
        viewModelScope.launch {
            getOpenBookmarkSidePreviewOnLargeScreenUseCase().collectLatest { enabled ->
                withState { current ->
                    if (current.openBookmarkSidePreviewOnLargeScreen == enabled) return@withState
                    setState { copy(openBookmarkSidePreviewOnLargeScreen = enabled) }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<BehaviorViewModel, BehaviorState> {
        override fun create(state: BehaviorState): BehaviorViewModel
    }

    companion object : MavericksViewModelFactory<BehaviorViewModel, BehaviorState> by hiltMavericksViewModelFactory()
}
