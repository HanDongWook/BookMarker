package com.hdw.bookmarker.feature.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.core.domain.usecase.GetBookmarksUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.model.bookmark.Bookmark
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.model.browser.BrowserInfo
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
    object OpenFilePicker : MainSideEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase
) : ViewModel(),
    ContainerHost<MainState, MainSideEffect> {

    override val container = container<MainState, MainSideEffect>(MainState()) {
        loadInstalledBrowsers()
    }

    private fun loadInstalledBrowsers() = intent {
        val browsers = getInstalledBrowsersUseCase()
        reduce { state.copy(installedBrowsers = browsers) }
    }

    fun openFilePicker() = intent {
        Timber.d("Opening file picker")
        postSideEffect(MainSideEffect.OpenFilePicker)
    }

    fun onSyncClick(browser: BrowserInfo) = intent {
        Timber.d("Syncing browser: ${browser.appName}")
        openFilePicker()
    }

    fun onHtmlFileSelected(uri: Uri) = intent {
        Timber.d("Selected html file uri: $uri")
    }

    private fun getBookmarks() = intent {
        val bookmarks = getBookmarksUseCase(browser = Browser.CHROME)
    }
}
