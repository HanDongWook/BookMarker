package com.hdw.bookmarker.feature.home

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.core.domain.usecase.GetBookmarksUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
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
    val connectedBrowserPackages: Set<String> = emptySet(),
    val bookmarkDocuments: Map<String, BookmarkDocument> = emptyMap(),
    val selectedBrowserPackage: String? = null,
    val isLoading: Boolean = false,
)

sealed interface MainSideEffect {
    data class ShowSyncStarted(val browserName: String) : MainSideEffect
    data class ShowError(
        @param:StringRes val messageResId: Int,
        val detail: String? = null
    ) : MainSideEffect

    object OpenFilePicker : MainSideEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase
) : ViewModel(),
    ContainerHost<MainState, MainSideEffect> {

    private var pendingSyncBrowserPackage: String? = null

    override val container = container<MainState, MainSideEffect>(MainState()) {
        loadInstalledBrowsers()
    }

    private fun loadInstalledBrowsers() = intent {
        val browsers = getInstalledBrowsersUseCase()
        reduce {
            state.copy(
                installedBrowsers = browsers,
                selectedBrowserPackage = state.selectedBrowserPackage ?: browsers.firstOrNull()?.packageName
            )
        }
    }

    fun openFilePicker() = intent {
        Timber.e("Opening file picker")
        postSideEffect(MainSideEffect.OpenFilePicker)
    }

    fun onSyncClick(browser: BrowserInfo) = intent {
        Timber.e("Syncing browser: ${browser.appName}")
        pendingSyncBrowserPackage = browser.packageName
        reduce { state.copy(selectedBrowserPackage = browser.packageName) }
        openFilePicker()
    }

    fun onBrowserSelected(packageName: String) = intent {
        reduce { state.copy(selectedBrowserPackage = packageName) }
    }

    fun onHtmlFileSelected(uri: Uri) = intent {
        Timber.e("Selected html file uri: $uri")
        val targetBrowserPackage = pendingSyncBrowserPackage
        when (val result = getBookmarksUseCase(browser = Browser.CHROME, uri = uri)) {
            is BookmarkImportResult.Success -> {
                if (targetBrowserPackage != null) {
                    reduce {
                        state.copy(
                            connectedBrowserPackages = state.connectedBrowserPackages + targetBrowserPackage,
                            bookmarkDocuments = state.bookmarkDocuments + (targetBrowserPackage to result.document),
                            selectedBrowserPackage = targetBrowserPackage
                        )
                    }
                }
            }

            is BookmarkImportResult.Failure -> {
                Timber.e("Bookmark html import failed. error=%s, message=%s", result.error, result.message)
                postSideEffect(
                    MainSideEffect.ShowError(
                        messageResId = result.error.toUiMessageResId(),
                        detail = result.message
                    )
                )
            }
        }
        pendingSyncBrowserPackage = null
    }

    @StringRes
    private fun BookmarkImportError.toUiMessageResId(): Int {
        return when (this) {
            BookmarkImportError.INVALID_URI -> R.string.home_error_invalid_uri
            BookmarkImportError.FILE_NOT_FOUND -> R.string.home_error_file_not_found
            BookmarkImportError.PERMISSION_DENIED -> R.string.home_error_permission_denied
            BookmarkImportError.IO_ERROR -> R.string.home_error_io
            BookmarkImportError.EMPTY_CONTENT -> R.string.home_error_empty_content
            BookmarkImportError.PARSE_ERROR -> R.string.home_error_parse
            BookmarkImportError.UNSUPPORTED_BROWSER -> R.string.home_error_unsupported_browser
            BookmarkImportError.UNKNOWN -> R.string.home_error_unknown
        }
    }
}
