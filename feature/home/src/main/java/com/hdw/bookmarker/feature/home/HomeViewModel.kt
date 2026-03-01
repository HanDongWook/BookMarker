package com.hdw.bookmarker.feature.home

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.core.domain.usecase.ClearBookmarkSnapshotUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkColorsUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkDisplayTypeUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkRawFileHashUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkSnapshotsUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarksUseCase
import com.hdw.bookmarker.core.domain.usecase.GetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.domain.usecase.GetOrderedSnapshotIdsUseCase
import com.hdw.bookmarker.core.domain.usecase.SaveBookmarkSnapshotUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkColorUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkDisplayTypeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.error.BookmarkImportError
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import com.hdw.bookmarker.core.model.file.error.ContentFileError
import com.hdw.bookmarker.core.model.file.result.ContentFileResult
import com.hdw.bookmarker.feature.home.boomarkcontent.BookmarkDisplayType
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

data class HomeState(
    val installedBrowsers: List<BrowserInfo> = emptyList(),
    val orderedSnapshotIds: List<String> = emptyList(),
    val bookmarkDocuments: Map<String, BookmarkDocument> = emptyMap(),
    val bookmarkColors: Map<String, Long> = emptyMap(),
    val selectedBookmarkId: String? = null,
    val defaultBrowserPackage: String? = null,
    val bookmarkDisplayType: BookmarkDisplayType = BookmarkDisplayType.LIST,
    val isImporting: Boolean = false,
)

sealed interface HomeSideEffect {
    data class ShowMessage(@param:StringRes val messageResId: Int) : HomeSideEffect
    data class ShowError(@param:StringRes val messageResId: Int, val detail: String? = null) : HomeSideEffect
    object OpenFilePicker : HomeSideEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val getBookmarkRawFileHashUseCase: GetBookmarkRawFileHashUseCase,
    private val getBookmarkSnapshotsUseCase: GetBookmarkSnapshotsUseCase,
    private val getOrderedSnapshotIdsUseCase: GetOrderedSnapshotIdsUseCase,
    private val getBookmarkColorsUseCase: GetBookmarkColorsUseCase,
    private val saveBookmarkSnapshotUseCase: SaveBookmarkSnapshotUseCase,
    private val setBookmarkColorUseCase: SetBookmarkColorUseCase,
    private val clearBookmarkSnapshotUseCase: ClearBookmarkSnapshotUseCase,
    private val getDefaultBrowserPackageUseCase: GetDefaultBrowserPackageUseCase,
    private val setDefaultBrowserPackageUseCase: SetDefaultBrowserPackageUseCase,
    private val getBookmarkDisplayTypeUseCase: GetBookmarkDisplayTypeUseCase,
    private val setBookmarkDisplayTypeUseCase: SetBookmarkDisplayTypeUseCase,
) : ViewModel(),
    ContainerHost<HomeState, HomeSideEffect> {
    private var isObservingSnapshots = false
    private var isObservingOrderedIds = false
    private var isObservingColors = false
    private var isObservingDefaultBrowser = false
    private var isObservingBookmarkDisplayType = false

    override val container = container<HomeState, HomeSideEffect>(HomeState()) {
        observeBookmarkSnapshots()
        observeOrderedSnapshotIds()
        observeBookmarkColors()
        observeDefaultBrowserPackage()
        observeBookmarkDisplayType()
        loadInstalledBrowsers()
    }

    private fun loadInstalledBrowsers() = intent {
        val browsers = getInstalledBrowsersUseCase()
        reduce {
            state.copy(installedBrowsers = browsers)
        }
    }

    private fun observeBookmarkColors() = intent {
        if (isObservingColors) return@intent
        isObservingColors = true
        getBookmarkColorsUseCase().collect { colors ->
            reduce {
                state.copy(bookmarkColors = colors)
            }
        }
    }

    private fun observeBookmarkSnapshots() = intent {
        if (isObservingSnapshots) return@intent
        isObservingSnapshots = true
        getBookmarkSnapshotsUseCase().collect { snapshots ->
            reduce {
                state.copy(
                    bookmarkDocuments = snapshots,
                    selectedBookmarkId = state.selectedBookmarkId
                        ?.takeIf { snapshots.containsKey(it) }
                        ?: snapshots.keys.firstOrNull(),
                )
            }
        }
    }

    private fun observeOrderedSnapshotIds() = intent {
        if (isObservingOrderedIds) return@intent
        isObservingOrderedIds = true
        getOrderedSnapshotIdsUseCase().collect { ids ->
            reduce {
                state.copy(
                    orderedSnapshotIds = ids,
                    selectedBookmarkId = state.selectedBookmarkId
                        ?.takeIf { ids.contains(it) }
                        ?: ids.firstOrNull(),
                )
            }
        }
    }

    fun openFilePicker() = intent {
        postSideEffect(HomeSideEffect.OpenFilePicker)
    }

    fun onSnapshotSelected(snapshotId: String) = intent {
        if (state.selectedBookmarkId == snapshotId) return@intent
        reduce { state.copy(selectedBookmarkId = snapshotId) }
    }

    fun onDefaultBrowserSelected(packageName: String) = intent {
        setDefaultBrowserPackageUseCase(packageName)
    }

    fun onBookmarkDisplayTypeToggle() = intent {
        val nextType = when (state.bookmarkDisplayType) {
            BookmarkDisplayType.LIST -> BookmarkDisplayType.ICON
            BookmarkDisplayType.ICON -> BookmarkDisplayType.LIST
        }
        setBookmarkDisplayTypeUseCase(nextType.name)
    }

    fun onHtmlFileSelected(uri: Uri) = intent {
        reduce { state.copy(isImporting = true) }
        try {
            val rawFileHash = when (val hashResult = getBookmarkRawFileHashUseCase(uri)) {
                is ContentFileResult.Success -> hashResult.data

                is ContentFileResult.Failure -> {
                    postSideEffect(
                        HomeSideEffect.ShowError(
                            messageResId = hashResult.error.toUiMessageResId(),
                            detail = hashResult.message,
                        ),
                    )
                    return@intent
                }
            }

            when (val result = getBookmarksUseCase(browser = Browser.CHROME, uri = uri)) {
                is BookmarkImportResult.Success -> {
                    val savedId = saveBookmarkSnapshotUseCase(
                        snapshotId = null,
                        document = result.document,
                        sourceHash = rawFileHash,
                    )
                    reduce { state.copy(selectedBookmarkId = savedId) }
                }

                is BookmarkImportResult.Failure -> {
                    Timber.e("Bookmark html import failed. error=%s, message=%s", result.error, result.message)
                    postSideEffect(
                        HomeSideEffect.ShowError(
                            messageResId = result.error.toUiMessageResId(),
                            detail = result.message,
                        ),
                    )
                }
            }
        } finally {
            reduce { state.copy(isImporting = false) }
        }
    }

    fun onBookmarkColorSelected(snapshotId: String, bookmarkColor: Long) = intent {
        setBookmarkColorUseCase(snapshotId, bookmarkColor)
    }

    fun deleteBookmarkSnapshot(snapshotId: String) = intent {
        clearBookmarkSnapshotUseCase(snapshotId)
        val updatedIds = state.orderedSnapshotIds - snapshotId
        reduce {
            state.copy(
                selectedBookmarkId = state.selectedBookmarkId
                    ?.takeIf { it != snapshotId }
                    ?: updatedIds.firstOrNull(),
            )
        }
    }

    private fun observeDefaultBrowserPackage() = intent {
        if (isObservingDefaultBrowser) return@intent
        isObservingDefaultBrowser = true
        getDefaultBrowserPackageUseCase().collect { browserInfo ->
            reduce {
                state.copy(
                    defaultBrowserPackage = browserInfo?.packageName,
                )
            }
        }
    }

    private fun observeBookmarkDisplayType() = intent {
        if (isObservingBookmarkDisplayType) return@intent
        isObservingBookmarkDisplayType = true
        getBookmarkDisplayTypeUseCase().collect { displayType ->
            reduce {
                state.copy(
                    bookmarkDisplayType = displayType.toBookmarkDisplayType(),
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

    @StringRes
    private fun ContentFileError.toUiMessageResId(): Int = when (this) {
        ContentFileError.INVALID_URI -> R.string.error_invalid_uri
        ContentFileError.FILE_NOT_FOUND -> R.string.error_file_not_found
        ContentFileError.PERMISSION_DENIED -> R.string.error_permission_denied
        ContentFileError.IO_ERROR -> R.string.error_io
        ContentFileError.EMPTY_CONTENT -> R.string.error_empty_content
        ContentFileError.UNKNOWN -> R.string.error_unknown
    }

    private fun String?.toBookmarkDisplayType(): BookmarkDisplayType = when (this) {
        BookmarkDisplayType.ICON.name -> BookmarkDisplayType.ICON
        BookmarkDisplayType.LIST.name -> BookmarkDisplayType.LIST
        else -> BookmarkDisplayType.LIST
    }
}
