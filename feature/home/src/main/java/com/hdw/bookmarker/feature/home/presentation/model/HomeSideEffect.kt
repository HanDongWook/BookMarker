package com.hdw.bookmarker.feature.home.presentation.model

import androidx.annotation.StringRes

sealed interface HomeSideEffect {
    data class ShowMessage(@param:StringRes val messageResId: Int) : HomeSideEffect
    data class ShowError(@param:StringRes val messageResId: Int, val detail: String? = null) : HomeSideEffect
}
