package com.hdw.bookmarker.feature.home

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkRawFileHashUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkSnapshotRawFileHashUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkSnapshotsUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarksUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.domain.usecase.SaveBookmarkSnapshotUseCase
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.error.BookmarkImportError
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import com.hdw.bookmarker.core.model.file.error.ContentFileError
import com.hdw.bookmarker.core.model.file.result.ContentFileResult
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
)

sealed interface MainSideEffect {
    data class ShowMessage(@param:StringRes val messageResId: Int) : MainSideEffect
    data class ShowError(@param:StringRes val messageResId: Int, val detail: String? = null) : MainSideEffect
    object ShowOverwriteConfirmDialog : MainSideEffect

    object OpenFilePicker : MainSideEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val getBookmarkRawFileHashUseCase: GetBookmarkRawFileHashUseCase,
    private val getBookmarkSnapshotRawFileHashUseCase: GetBookmarkSnapshotRawFileHashUseCase,
    private val getBookmarkSnapshotsUseCase: GetBookmarkSnapshotsUseCase,
    private val saveBookmarkSnapshotUseCase: SaveBookmarkSnapshotUseCase,
) : ViewModel(),
    ContainerHost<MainState, MainSideEffect> {
    private data class PendingOverwriteImport(val uri: Uri, val browserPackage: String, val rawFileHash: String)

    private var isObservingSnapshots = false
    private var pendingOverwriteImport: PendingOverwriteImport? = null

    override val container = container<MainState, MainSideEffect>(MainState()) {
        observeBookmarkSnapshots()
        loadInstalledBrowsers()
    }

    private fun loadInstalledBrowsers() = intent {
        val browsers = getInstalledBrowsersUseCase()
        val installedPackages = browsers.map(BrowserInfo::packageName).toSet()
        reduce {
            state.copy(
                installedBrowsers = browsers,
                connectedBrowserPackages = state.bookmarkDocuments.keys.intersect(installedPackages),
                selectedBrowserPackage = state.selectedBrowserPackage
                    ?.takeIf { selected -> installedPackages.contains(selected) }
                    ?: browsers.firstOrNull()?.packageName,
            )
        }
    }

    private fun observeBookmarkSnapshots() = intent {
        if (isObservingSnapshots) return@intent
        isObservingSnapshots = true
        getBookmarkSnapshotsUseCase().collect { snapshots ->
            val installedPackages = state.installedBrowsers.map(BrowserInfo::packageName).toSet()
            reduce {
                state.copy(
                    bookmarkDocuments = snapshots,
                    connectedBrowserPackages = snapshots.keys.intersect(installedPackages),
                    selectedBrowserPackage = state.selectedBrowserPackage
                        ?.takeIf { selected -> installedPackages.contains(selected) }
                        ?: state.installedBrowsers.firstOrNull { snapshots.containsKey(it.packageName) }?.packageName
                        ?: state.installedBrowsers.firstOrNull()?.packageName,
                )
            }
        }
    }

    fun openFilePicker() = intent {
        postSideEffect(MainSideEffect.OpenFilePicker)
    }

    fun onBrowserSelected(packageName: String) = intent {
        if (state.selectedBrowserPackage == packageName) return@intent
        reduce { state.copy(selectedBrowserPackage = packageName) }
    }

    fun onHtmlFileSelected(uri: Uri) = intent {
        val targetBrowserPackage = state.selectedBrowserPackage ?: return@intent
        val rawFileHash = when (val hashResult = getBookmarkRawFileHashUseCase(uri)) {
            is ContentFileResult.Success -> hashResult.data

            is ContentFileResult.Failure -> {
                postSideEffect(
                    MainSideEffect.ShowError(
                        messageResId = hashResult.error.toUiMessageResId(),
                        detail = hashResult.message,
                    ),
                )
                return@intent
            }
        }

        val existingRawFileHash = getBookmarkSnapshotRawFileHashUseCase(targetBrowserPackage)
        if (existingRawFileHash == rawFileHash) {
            postSideEffect(MainSideEffect.ShowMessage(R.string.import_skipped_same_file))
            return@intent
        }

        if (!existingRawFileHash.isNullOrBlank()) {
            pendingOverwriteImport = PendingOverwriteImport(
                uri = uri,
                browserPackage = targetBrowserPackage,
                rawFileHash = rawFileHash,
            )
            postSideEffect(MainSideEffect.ShowOverwriteConfirmDialog)
            return@intent
        }

        when (val result = getBookmarksUseCase(browser = Browser.CHROME, uri = uri)) {
            is BookmarkImportResult.Success -> {
                reduce {
                    state.copy(
                        connectedBrowserPackages = state.connectedBrowserPackages + targetBrowserPackage,
                        bookmarkDocuments = state.bookmarkDocuments + (targetBrowserPackage to result.document),
                        selectedBrowserPackage = targetBrowserPackage,
                    )
                }
                saveBookmarkSnapshotUseCase(
                    browserPackage = targetBrowserPackage,
                    document = result.document,
                    sourceHash = rawFileHash,
                )
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

    fun confirmOverwriteImport() = intent {
        val pending = pendingOverwriteImport ?: return@intent
        pendingOverwriteImport = null
        when (val result = getBookmarksUseCase(browser = Browser.CHROME, uri = pending.uri)) {
            is BookmarkImportResult.Success -> {
                reduce {
                    state.copy(
                        connectedBrowserPackages = state.connectedBrowserPackages + pending.browserPackage,
                        bookmarkDocuments = state.bookmarkDocuments + (pending.browserPackage to result.document),
                        selectedBrowserPackage = pending.browserPackage,
                    )
                }
                saveBookmarkSnapshotUseCase(
                    browserPackage = pending.browserPackage,
                    document = result.document,
                    sourceHash = pending.rawFileHash,
                )
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

    fun cancelOverwriteImport() = intent {
        pendingOverwriteImport = null
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

    @StringRes
    private fun ContentFileError.toUiMessageResId(): Int = when (this) {
        ContentFileError.INVALID_URI -> R.string.error_invalid_uri
        ContentFileError.FILE_NOT_FOUND -> R.string.error_file_not_found
        ContentFileError.PERMISSION_DENIED -> R.string.error_permission_denied
        ContentFileError.IO_ERROR -> R.string.error_io
        ContentFileError.EMPTY_CONTENT -> R.string.error_empty_content
        ContentFileError.UNKNOWN -> R.string.error_unknown
    }
}
