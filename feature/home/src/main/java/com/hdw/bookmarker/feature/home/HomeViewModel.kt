package com.hdw.bookmarker.feature.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.core.domain.usecase.GetBookmarksUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.model.bookmark.Bookmark
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportError
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
                val message = result.error.toUiMessage(result.message)
                Timber.e("Bookmark html import failed. error=%s, message=%s", result.error, result.message)
                postSideEffect(MainSideEffect.ShowError(message))
            }
        }
    }

    private fun BookmarkImportError.toUiMessage(detail: String?): String {
        return when (this) {
            BookmarkImportError.INVALID_URI -> "파일 경로가 잘못되었어요."
            BookmarkImportError.FILE_NOT_FOUND -> "북마크 파일을 찾을 수 없어요."
            BookmarkImportError.PERMISSION_DENIED -> "파일 읽기 권한이 없어요."
            BookmarkImportError.IO_ERROR -> "파일을 읽는 중 오류가 발생했어요."
            BookmarkImportError.EMPTY_CONTENT -> "북마크 파일 내용이 비어 있어요."
            BookmarkImportError.PARSE_ERROR -> "북마크 형식을 해석하지 못했어요."
            BookmarkImportError.UNSUPPORTED_BROWSER -> "지원하지 않는 브라우저예요."
            BookmarkImportError.UNKNOWN -> "알 수 없는 오류가 발생했어요."
        } + if (detail.isNullOrBlank()) "" else " ($detail)"
    }
}
