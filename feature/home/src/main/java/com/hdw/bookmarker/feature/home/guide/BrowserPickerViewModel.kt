package com.hdw.bookmarker.feature.home.guide

import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class BrowserPickerState(
    val installedBrowsers: List<BrowserInfo> = emptyList(),
)

@HiltViewModel
class BrowserPickerViewModel @Inject constructor(
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
) : ViewModel(), ContainerHost<BrowserPickerState, Nothing> {
    override val container = container<BrowserPickerState, Nothing>(BrowserPickerState()) {
        loadInstalledBrowsers()
    }

    private fun loadInstalledBrowsers() = intent {
        val browsers = getInstalledBrowsersUseCase()
        reduce {
            state.copy(installedBrowsers = browsers)
        }
    }
}
