package com.hdw.bookmarker.feature.settings.model

sealed interface DisplayValueState {
    data object Loading : DisplayValueState

    data object Unavailable : DisplayValueState

    data class Loaded(val value: String) : DisplayValueState
}
