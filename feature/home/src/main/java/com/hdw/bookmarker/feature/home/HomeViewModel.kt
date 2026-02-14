package com.hdw.bookmarker.feature.home

import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.model.Bookmark
import com.hdw.bookmarker.model.BrowserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

data class MainState(
    val installedBrowsers: List<BrowserInfo> = emptyList(),
    val bookmarks: List<Bookmark> = emptyList(),
    val isLoading: Boolean = false,
)

sealed interface MainSideEffect {
    data class ShowSyncStarted(val browserName: String) : MainSideEffect
    data class ShowError(val message: String) : MainSideEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase) :
    ViewModel(),
    ContainerHost<MainState, MainSideEffect> {

    override val container = container<MainState, MainSideEffect>(MainState()) {
        loadInstalledBrowsers()
    }

    private fun loadInstalledBrowsers() = intent {
        val browsers = getInstalledBrowsersUseCase()
        reduce { state.copy(installedBrowsers = browsers) }
    }

    fun onSyncClick(browser: BrowserInfo) = intent {
        Timber.d("Syncing browser: ${browser.appName}")
        postSideEffect(MainSideEffect.ShowSyncStarted(browser.appName))
    }
}
