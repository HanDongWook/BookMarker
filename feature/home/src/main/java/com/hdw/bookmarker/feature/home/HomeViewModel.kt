package com.hdw.bookmarker.feature.home

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.core.domain.usecase.ClearBookmarkSnapshotUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkColorsUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkDisplayTypeUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkFolderIconStyleUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkRawFileHashUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkSnapshotRawFileHashUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkSnapshotsUseCase
import com.hdw.bookmarker.core.domain.usecase.GetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.domain.usecase.GetOrderedSnapshotIdsUseCase
import com.hdw.bookmarker.core.domain.usecase.GetScrollLongBookmarkUrlUseCase
import com.hdw.bookmarker.core.domain.usecase.GetScrollLongFolderDescriptionUseCase
import com.hdw.bookmarker.core.domain.usecase.GetShowBookmarkUrlUseCase
import com.hdw.bookmarker.core.domain.usecase.GetShowFolderDescriptionUseCase
import com.hdw.bookmarker.core.domain.usecase.ImportBookmarksFromHtmlUseCase
import com.hdw.bookmarker.core.domain.usecase.SaveBookmarkSnapshotUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkColorUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkDisplayTypeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.bookmark.error.BookmarkImportError
import com.hdw.bookmarker.core.model.bookmark.result.BookmarkImportResult
import com.hdw.bookmarker.core.model.file.error.ContentFileError
import com.hdw.bookmarker.core.model.file.result.ContentFileResult
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.home.contract.AddBookmarkItemRequest
import com.hdw.bookmarker.feature.home.contract.BookmarkDisplayType
import com.hdw.bookmarker.feature.home.contract.HomeSideEffect
import com.hdw.bookmarker.feature.home.contract.HomeState
import com.hdw.bookmarker.feature.home.contract.UpdateBookmarkItemRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
    private val importBookmarksFromHtmlUseCase: ImportBookmarksFromHtmlUseCase,
    private val getBookmarkRawFileHashUseCase: GetBookmarkRawFileHashUseCase,
    private val getBookmarkSnapshotRawFileHashUseCase: GetBookmarkSnapshotRawFileHashUseCase,
    private val getBookmarkSnapshotsUseCase: GetBookmarkSnapshotsUseCase,
    private val getOrderedSnapshotIdsUseCase: GetOrderedSnapshotIdsUseCase,
    private val getBookmarkColorsUseCase: GetBookmarkColorsUseCase,
    private val saveBookmarkSnapshotUseCase: SaveBookmarkSnapshotUseCase,
    private val setBookmarkColorUseCase: SetBookmarkColorUseCase,
    private val clearBookmarkSnapshotUseCase: ClearBookmarkSnapshotUseCase,
    private val getDefaultBrowserPackageUseCase: GetDefaultBrowserPackageUseCase,
    private val setDefaultBrowserPackageUseCase: SetDefaultBrowserPackageUseCase,
    private val getBookmarkDisplayTypeUseCase: GetBookmarkDisplayTypeUseCase,
    private val getScrollLongBookmarkUrlUseCase: GetScrollLongBookmarkUrlUseCase,
    private val getScrollLongFolderDescriptionUseCase: GetScrollLongFolderDescriptionUseCase,
    private val setBookmarkDisplayTypeUseCase: SetBookmarkDisplayTypeUseCase,
    private val getShowBookmarkUrlUseCase: GetShowBookmarkUrlUseCase,
    private val getShowFolderDescriptionUseCase: GetShowFolderDescriptionUseCase,
    private val getBookmarkFolderIconStyleUseCase: GetBookmarkFolderIconStyleUseCase,
) : ViewModel(),
    ContainerHost<HomeState, HomeSideEffect> {
    private var isObservingSnapshots = false
    private var isObservingOrderedIds = false
    private var isObservingColors = false
    private var isObservingDefaultBrowser = false
    private var isObservingBookmarkDisplayType = false
    private var isObservingScrollLongBookmarkUrl = false
    private var isObservingShowBookmarkUrl = false
    private var isObservingShowFolderDescription = false
    private var isObservingScrollLongFolderDescription = false
    private var isObservingFolderIconStyle = false

    override val container = container<HomeState, HomeSideEffect>(HomeState()) {
        observeBookmarkSnapshots()
        observeOrderedSnapshotIds()
        observeBookmarkColors()
        observeDefaultBrowserPackage()
        observeBookmarkDisplayType()
        observeScrollLongBookmarkUrl()
        observeShowBookmarkUrl()
        observeShowFolderDescription()
        observeScrollLongFolderDescription()
        observeFolderIconStyle()
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
            val nextSelectedBookmarkId = state.selectedBookmarkId
                ?.takeIf { snapshots.containsKey(it) }
                ?: snapshots.keys.firstOrNull()
            reduce {
                state.withSelectedBookmarkId(nextSelectedBookmarkId).copy(
                    bookmarkDocuments = snapshots,
                    selectedFolderPaths = state.selectedFolderPaths.retain(snapshots.keys),
                )
            }
        }
    }

    private fun observeOrderedSnapshotIds() = intent {
        if (isObservingOrderedIds) return@intent
        isObservingOrderedIds = true
        getOrderedSnapshotIdsUseCase().collect { ids ->
            val nextSelectedBookmarkId = state.selectedBookmarkId
                ?.takeIf { ids.contains(it) }
                ?: ids.firstOrNull()
            reduce {
                state.withSelectedBookmarkId(nextSelectedBookmarkId).copy(
                    orderedSnapshotIds = ids,
                    selectedFolderPaths = state.selectedFolderPaths.retain(ids),
                )
            }
        }
    }

    fun openFilePicker() = intent {
        postSideEffect(HomeSideEffect.OpenFilePicker)
    }

    fun onSnapshotSelected(snapshotId: String) = intent {
        if (state.selectedBookmarkId == snapshotId) return@intent
        reduce { state.withSelectedBookmarkId(snapshotId) }
    }

    fun onSelectedFolderPathChange(snapshotId: String, path: List<Int>?) = intent {
        reduce {
            state.copy(
                selectedFolderPaths = state.selectedFolderPaths.update(snapshotId, path),
            )
        }
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

            when (val result = importBookmarksFromHtmlUseCase(uri = uri)) {
                is BookmarkImportResult.Success -> {
                    val snapshotTitle = state.nextDefaultSnapshotTitle(context)
                    val savedId = saveBookmarkSnapshotUseCase(
                        snapshotId = null,
                        document = result.document.copy(title = snapshotTitle),
                        sourceHash = rawFileHash,
                    )
                    reduce { state.withSelectedBookmarkId(savedId) }
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
            state.withSelectedBookmarkId(
                state.selectedBookmarkId
                    ?.takeIf { it != snapshotId }
                    ?: updatedIds.firstOrNull(),
            ).copy(
                selectedFolderPaths = state.selectedFolderPaths.remove(snapshotId),
            )
        }
    }

    fun addEmptyBookmarkSnapshot() = intent {
        val snapshotTitle = state.nextDefaultSnapshotTitle(context)
        val snapshotId = saveBookmarkSnapshotUseCase(
            snapshotId = null,
            document = BookmarkDocument(
                title = snapshotTitle,
                metas = emptyMap(),
                rootItems = emptyList(),
            ),
            sourceHash = "",
        )
        reduce { state.withSelectedBookmarkId(snapshotId) }
    }

    fun addBookmarkItem(request: AddBookmarkItemRequest) = intent {
        val trimmedTitle = request.title.trim()
        if (trimmedTitle.isBlank()) return@intent

        val now = currentEpochSecondsString()
        val item = when (request) {
            is AddBookmarkItemRequest.Folder -> BookmarkItem.Folder(
                title = trimmedTitle,
                description = request.description.trim().takeIf { it.isNotBlank() },
                addDate = now,
                lastModified = now,
                children = emptyList(),
            )

            is AddBookmarkItemRequest.Bookmark -> {
                val trimmedUrl = request.url.trim()
                if (trimmedUrl.isBlank()) return@intent
                BookmarkItem.Bookmark(
                    title = trimmedTitle,
                    url = normalizeUrl(trimmedUrl),
                    addDate = now,
                    lastModified = now,
                    iconUri = null,
                )
            }
        }

        val currentState = state
        val savedSnapshotId = saveAddedItem(
            currentState = currentState,
            parentFolderPath = request.parentFolderPath,
            item = item,
        )
        reduce { state.withSelectedBookmarkId(savedSnapshotId) }
    }

    fun deleteBookmarkItem(path: List<Int>) = intent {
        if (path.isEmpty()) return@intent

        val currentSnapshotId = state.selectedBookmarkId ?: return@intent
        val currentDocument = state.bookmarkDocuments[currentSnapshotId] ?: return@intent
        val updatedRootItems = removeItemByPath(currentDocument.rootItems, path) ?: return@intent
        val sourceHash = getBookmarkSnapshotRawFileHashUseCase(currentSnapshotId).orEmpty()

        val savedSnapshotId = saveBookmarkSnapshotUseCase(
            snapshotId = currentSnapshotId,
            document = currentDocument.copy(rootItems = updatedRootItems),
            sourceHash = sourceHash,
        )
        reduce { state.withSelectedBookmarkId(savedSnapshotId) }
    }

    fun updateBookmarkItem(request: UpdateBookmarkItemRequest) = intent {
        if (request.path.isEmpty()) return@intent
        val trimmedTitle = request.title.trim()
        if (trimmedTitle.isBlank()) return@intent
        val normalizedRequest = when (request) {
            is UpdateBookmarkItemRequest.Bookmark -> request.copy(title = trimmedTitle)
            is UpdateBookmarkItemRequest.Folder -> request.copy(title = trimmedTitle)
        }

        val currentSnapshotId = state.selectedBookmarkId ?: return@intent
        val currentDocument = state.bookmarkDocuments[currentSnapshotId] ?: return@intent
        val updatedRootItems = updateItemByPath(
            items = currentDocument.rootItems,
            path = normalizedRequest.path,
            request = normalizedRequest,
        ) ?: return@intent
        val sourceHash = getBookmarkSnapshotRawFileHashUseCase(currentSnapshotId).orEmpty()

        val savedSnapshotId = saveBookmarkSnapshotUseCase(
            snapshotId = currentSnapshotId,
            document = currentDocument.copy(rootItems = updatedRootItems),
            sourceHash = sourceHash,
        )
        reduce { state.copy(selectedBookmarkId = savedSnapshotId) }
    }

    fun renameBookmarkSnapshot(snapshotId: String, title: String) = intent {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) return@intent

        val currentDocument = state.bookmarkDocuments[snapshotId] ?: return@intent
        val sourceHash = getBookmarkSnapshotRawFileHashUseCase(snapshotId).orEmpty()

        val savedSnapshotId = saveBookmarkSnapshotUseCase(
            snapshotId = snapshotId,
            document = currentDocument.copy(title = trimmedTitle),
            sourceHash = sourceHash,
        )
        reduce { state.copy(selectedBookmarkId = savedSnapshotId) }
    }

    private suspend fun saveAddedItem(
        currentState: HomeState,
        item: BookmarkItem,
        parentFolderPath: List<Int>? = null,
    ): String {
        val currentSnapshotId = currentState.selectedBookmarkId
        val currentDocument = currentSnapshotId
            ?.let { currentState.bookmarkDocuments[it] }
            ?: BookmarkDocument(
                title = currentState.nextDefaultSnapshotTitle(context),
                metas = emptyMap(),
                rootItems = emptyList(),
            )

        val updatedRootItems = if (parentFolderPath.isNullOrEmpty()) {
            currentDocument.rootItems + item
        } else {
            addItemToFolderByPath(currentDocument.rootItems, parentFolderPath, item)
                ?: (currentDocument.rootItems + item)
        }

        val updatedDocument = currentDocument.copy(
            rootItems = updatedRootItems,
        )

        val sourceHash = currentSnapshotId
            ?.let { getBookmarkSnapshotRawFileHashUseCase(it) }
            .orEmpty()

        return saveBookmarkSnapshotUseCase(
            snapshotId = currentSnapshotId,
            document = updatedDocument,
            sourceHash = sourceHash,
        )
    }

    private fun normalizeUrl(url: String): String {
        val trimmedUrl = url.trim()
        return if (trimmedUrl.contains("://")) trimmedUrl else "https://$trimmedUrl"
    }

    private fun removeItemByPath(items: List<BookmarkItem>, path: List<Int>): List<BookmarkItem>? {
        val targetIndex = path.firstOrNull() ?: return null
        if (targetIndex !in items.indices) return null

        if (path.size == 1) {
            return items.toMutableList().apply { removeAt(targetIndex) }
        }

        val targetFolder = items[targetIndex] as? BookmarkItem.Folder ?: return null
        val updatedChildren = removeItemByPath(targetFolder.children, path.drop(1)) ?: return null

        return items.toMutableList().apply {
            this[targetIndex] = targetFolder.copy(children = updatedChildren)
        }
    }

    private fun addItemToFolderByPath(
        items: List<BookmarkItem>,
        path: List<Int>,
        item: BookmarkItem,
    ): List<BookmarkItem>? {
        val targetIndex = path.firstOrNull() ?: return null
        if (targetIndex !in items.indices) return null

        val targetFolder = items[targetIndex] as? BookmarkItem.Folder ?: return null
        val updatedFolder = if (path.size == 1) {
            targetFolder.copy(children = targetFolder.children + item)
        } else {
            val updatedChildren = addItemToFolderByPath(targetFolder.children, path.drop(1), item) ?: return null
            targetFolder.copy(children = updatedChildren)
        }

        return items.toMutableList().apply {
            this[targetIndex] = updatedFolder
        }
    }

    private fun updateItemByPath(
        items: List<BookmarkItem>,
        path: List<Int>,
        request: UpdateBookmarkItemRequest,
    ): List<BookmarkItem>? {
        val targetIndex = path.firstOrNull() ?: return null
        if (targetIndex !in items.indices) return null

        if (path.size == 1) {
            val now = currentEpochSecondsString()
            val target = items[targetIndex]
            val updatedItem = when {
                target is BookmarkItem.Folder && request is UpdateBookmarkItemRequest.Folder -> target.copy(
                    title = request.title,
                    description = request.description.trim().takeIf { it.isNotBlank() },
                    lastModified = now,
                )

                target is BookmarkItem.Bookmark && request is UpdateBookmarkItemRequest.Bookmark -> {
                    val trimmedUrl = request.url.trim()
                    if (trimmedUrl.isBlank()) return null
                    target.copy(
                        title = request.title,
                        url = normalizeUrl(trimmedUrl),
                        lastModified = now,
                    )
                }

                else -> return null
            }

            return items.toMutableList().apply {
                this[targetIndex] = updatedItem
            }
        }

        val targetFolder = items[targetIndex] as? BookmarkItem.Folder ?: return null
        val updatedChildren = updateItemByPath(
            items = targetFolder.children,
            path = path.drop(1),
            request = request,
        ) ?: return null

        return items.toMutableList().apply {
            this[targetIndex] = targetFolder.copy(children = updatedChildren)
        }
    }

    private fun currentEpochSecondsString(): String = (System.currentTimeMillis() / 1000L).toString()

    private fun HomeState.nextDefaultSnapshotTitle(context: Context): String {
        val titlePrefix = context.getString(R.string.default_snapshot_title_prefix)
        val maxNumber = bookmarkDocuments.values
            .mapNotNull { document ->
                document.title
                    ?.trim()
                    ?.takeIf { it.startsWith(titlePrefix) }
                    ?.removePrefix(titlePrefix)
                    ?.toIntOrNull()
            }
            .maxOrNull()
            ?: 0
        return "$titlePrefix${maxNumber + 1}"
    }

    private fun HomeState.withSelectedBookmarkId(selectedBookmarkId: String?): HomeState = copy(
        selectedBookmarkId = selectedBookmarkId,
    )

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

    private fun observeFolderIconStyle() = intent {
        if (isObservingFolderIconStyle) return@intent
        isObservingFolderIconStyle = true
        getBookmarkFolderIconStyleUseCase().collect { style ->
            reduce {
                state.copy(folderIconStyle = style)
            }
        }
    }

    private fun observeScrollLongBookmarkUrl() = intent {
        if (isObservingScrollLongBookmarkUrl) return@intent
        isObservingScrollLongBookmarkUrl = true
        getScrollLongBookmarkUrlUseCase().collect { scrollLongBookmarkUrl ->
            reduce {
                state.copy(scrollLongBookmarkUrl = scrollLongBookmarkUrl)
            }
        }
    }

    private fun observeShowBookmarkUrl() = intent {
        if (isObservingShowBookmarkUrl) return@intent
        isObservingShowBookmarkUrl = true
        getShowBookmarkUrlUseCase().collect { showBookmarkUrl ->
            reduce {
                state.copy(showBookmarkUrl = showBookmarkUrl)
            }
        }
    }

    private fun observeShowFolderDescription() = intent {
        if (isObservingShowFolderDescription) return@intent
        isObservingShowFolderDescription = true
        getShowFolderDescriptionUseCase().collect { showFolderDescription ->
            reduce {
                state.copy(showFolderDescription = showFolderDescription)
            }
        }
    }

    private fun observeScrollLongFolderDescription() = intent {
        if (isObservingScrollLongFolderDescription) return@intent
        isObservingScrollLongFolderDescription = true
        getScrollLongFolderDescriptionUseCase().collect { scrollLongFolderDescription ->
            reduce {
                state.copy(scrollLongFolderDescription = scrollLongFolderDescription)
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
