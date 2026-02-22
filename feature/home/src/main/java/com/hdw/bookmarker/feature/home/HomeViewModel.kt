package com.hdw.bookmarker.feature.home

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.core.domain.usecase.GetBookmarksUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.model.bookmark.Bookmark
import com.hdw.bookmarker.core.model.bookmark.error.BookmarkImportError
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
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
    data class ShowError(@param:StringRes val messageResId: Int, val detail: String? = null) : MainSideEffect

    object OpenFilePicker : MainSideEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase,
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
        Timber.e("Opening file picker")
        postSideEffect(MainSideEffect.OpenFilePicker)
    }

    fun onSyncClick(browser: BrowserInfo) = intent {
        Timber.e("Syncing browser: ${browser.appName}")
        openFilePicker()
    }

    fun onHtmlFileSelected(uri: Uri) = intent {
        Timber.e("Selected html file uri: $uri")
        when (val result = getBookmarksUseCase(browser = Browser.CHROME, uri = uri)) {
            is BookmarkImportResult.Success -> {
                val parsedCount = result.document.rootItems.size
                Timber.d("Bookmark html imported successfully. rootItems=%d", parsedCount)
            }

            is BookmarkImportResult.Failure -> {
                Timber.e("Bookmark html import failed. error=%s, message=%s", result.error, result.message)
                postSideEffect(
                    MainSideEffect.ShowError(
                        messageResId = result.error.toUiMessageResId(),
                        detail = result.message,
                    ),
                )
            }
        }
    }

    @StringRes
    private fun BookmarkImportError.toUiMessageResId(): Int = when (this) {
        BookmarkImportError.INVALID_URI -> R.string.error_invalid_uri
        BookmarkImportError.FILE_NOT_FOUND -> R.string.error_file_not_found
        BookmarkImportError.PERMISSION_DENIED -> R.string.error_permission_denied
        BookmarkImportError.IO_ERROR -> R.string.error_io
        BookmarkImportError.EMPTY_CONTENT -> R.string.error_empty_content
        BookmarkImportError.PARSE_ERROR -> R.string.error_parse
        BookmarkImportError.UNSUPPORTED_BROWSER -> R.string.error_unsupported_browser
        BookmarkImportError.UNKNOWN -> R.string.error_unknown
    }
}
