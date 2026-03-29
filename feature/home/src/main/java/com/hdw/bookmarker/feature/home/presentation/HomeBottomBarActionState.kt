package com.hdw.bookmarker.feature.home.presentation

data class HomeBottomBarActionState(
    val showAddButton: Boolean = false,
    val onAddButtonClick: (() -> Unit)? = null,
)
