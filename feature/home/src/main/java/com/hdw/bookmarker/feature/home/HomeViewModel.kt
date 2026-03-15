package com.hdw.bookmarker.feature.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.core.domain.usecase.ClearBookmarkSnapshotUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.domain.usecase.ObserveHomeUiStateUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkColorUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkDisplayTypeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.model.bookmark.BookmarkItem
import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.core.model.bookmark.isInboxSnapshot
import com.hdw.bookmarker.feature.home.contract.AddBookmarkItemRequest
import com.hdw.bookmarker.feature.home.contract.BookmarkDisplayType
import com.hdw.bookmarker.feature.home.contract.BookmarkSnapshots
import com.hdw.bookmarker.feature.home.contract.HomeSideEffect
import com.hdw.bookmarker.feature.home.contract.HomeState
import com.hdw.bookmarker.feature.home.contract.UpdateBookmarkItemRequest
import com.hdw.bookmarker.feature.home.importer.BookmarkImportCoordinator
import com.hdw.bookmarker.feature.home.importer.BookmarkImportCoordinatorResult
import com.hdw.bookmarker.feature.home.snapshot.BookmarkSnapshotEditor
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
    private val observeHomeUiStateUseCase: ObserveHomeUiStateUseCase,
    private val setBookmarkColorUseCase: SetBookmarkColorUseCase,
    private val clearBookmarkSnapshotUseCase: ClearBookmarkSnapshotUseCase,
    private val setDefaultBrowserPackageUseCase: SetDefaultBrowserPackageUseCase,
    private val setBookmarkDisplayTypeUseCase: SetBookmarkDisplayTypeUseCase,
    private val bookmarkImportCoordinator: BookmarkImportCoordinator,
    private val bookmarkSnapshotEditor: BookmarkSnapshotEditor,
) : ViewModel(),
    ContainerHost<HomeState, HomeSideEffect> {
    override val container = container<HomeState, HomeSideEffect>(HomeState()) {
        observeHomeUiState()
        loadInstalledBrowsers()
    }

    private fun HomeState.withSelectedBookmarkId(selectedBookmarkId: SnapshotId?): HomeState = copy(
        selectedBookmarkId = selectedBookmarkId,
    )

    private fun observeHomeUiState() = intent {
        observeHomeUiStateUseCase().collect { observedState ->
            reduce {
                // Inbox를 앞으로 정렬한 뒤 BookmarkSnapshots로 통합
                val reorderedIds = observedState.orderedSnapshotIds.reorderInboxFirst(
                    observedState.bookmarkDocuments,
                )
                val snapshots = BookmarkSnapshots.of(
                    orderedIds = reorderedIds,
                    documents = observedState.bookmarkDocuments,
                )

                val visibleIds = snapshots.orderedIds.filter(snapshots::containsKey)
                val nextSelectedBookmarkId = state.selectedBookmarkId
                    ?.takeIf { id ->
                        snapshots.containsKey(id) &&
                            (visibleIds.isEmpty() || visibleIds.contains(id))
                    }
                    ?: visibleIds.firstOrNull()
                    ?: snapshots.orderedIds.firstOrNull()

                state.withSelectedBookmarkId(nextSelectedBookmarkId).copy(
                    bookmarkSnapshots = snapshots,
                    bookmarkColors = observedState.bookmarkColors.entries.associate { (id, color) ->
                        SnapshotId(id) to color
                    },
                    selectedFolderPaths = state.selectedFolderPaths.retain(snapshots.orderedIds),
                    defaultBrowserPackage = observedState.defaultBrowserPackage,
                    bookmarkDisplayType = observedState.bookmarkDisplayType.toBookmarkDisplayType(),
                    scrollLongBookmarkUrl = observedState.scrollLongBookmarkUrl,
                    showBookmarkUrl = observedState.showBookmarkUrl,
                    openBookmarkAdjacentOnLargeScreen =
                    observedState.openBookmarkAdjacentOnLargeScreen,
                    openBookmarkSidePreviewOnLargeScreen =
                    observedState.openBookmarkSidePreviewOnLargeScreen,
                    showFolderDescription = observedState.showFolderDescription,
                    scrollLongFolderDescription = observedState.scrollLongFolderDescription,
                    folderIconStyle = observedState.folderIconStyle,
                )
            }
        }
    }

    private fun loadInstalledBrowsers() = intent {
        val browsers = getInstalledBrowsersUseCase()
        reduce {
            state.copy(installedBrowsers = browsers)
        }
    }

    fun onSnapshotSelected(snapshotId: SnapshotId) = intent {
        if (state.selectedBookmarkId == snapshotId) return@intent
        reduce { state.withSelectedBookmarkId(snapshotId) }
    }

    fun onSelectedFolderPathChange(snapshotId: SnapshotId, path: List<Int>?) = intent {
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
            when (
                val result = bookmarkImportCoordinator.importHtml(
                    uri = uri,
                    existingDocuments = state.bookmarkSnapshots.values,
                )
            ) {
                is BookmarkImportCoordinatorResult.Success -> {
                    reduce { state.withSelectedBookmarkId(SnapshotId(result.snapshotId)) }
                }

                is BookmarkImportCoordinatorResult.Failure -> {
                    postSideEffect(
                        HomeSideEffect.ShowError(
                            messageResId = result.messageResId,
                            detail = result.detail,
                        ),
                    )
                }
            }
        } finally {
            reduce { state.copy(isImporting = false) }
        }
    }

    fun onBookmarkColorSelected(snapshotId: SnapshotId, bookmarkColor: Long) = intent {
        setBookmarkColorUseCase(snapshotId.value, bookmarkColor)
    }

    fun deleteBookmarkSnapshot(snapshotId: SnapshotId) = intent {
        clearBookmarkSnapshotUseCase(snapshotId.value)
        val updatedIds = state.bookmarkSnapshots.orderedIds - snapshotId
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
        val snapshotId = bookmarkSnapshotEditor.addEmptySnapshot(
            existingDocuments = state.bookmarkSnapshots.values,
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
                    description = request.description.trim().takeIf { it.isNotBlank() },
                    tags = normalizeTags(request.tags),
                )
            }
        }

        val snapshots = state.bookmarkSnapshots
        val savedSnapshotId = if (request is AddBookmarkItemRequest.Bookmark && request.saveToInbox) {
            bookmarkSnapshotEditor.addItemToInbox(
                library = snapshots,
                item = item,
            )
        } else {
            bookmarkSnapshotEditor.addItem(
                currentSnapshotId = state.selectedBookmarkId,
                library = snapshots,
                parentFolderPath = request.parentFolderPath,
                item = item,
            )
        }
        reduce { state.withSelectedBookmarkId(savedSnapshotId) }
    }

    fun deleteBookmarkItem(path: List<Int>) = intent {
        if (path.isEmpty()) return@intent

        val currentSnapshotId = state.selectedBookmarkId ?: return@intent
        val currentDocument = state.bookmarkSnapshots[currentSnapshotId] ?: return@intent
        val savedSnapshotId = bookmarkSnapshotEditor.deleteItem(
            snapshotId = currentSnapshotId,
            document = currentDocument,
            path = path,
        ) ?: return@intent
        reduce { state.withSelectedBookmarkId(savedSnapshotId) }
    }

    fun updateBookmarkItem(request: UpdateBookmarkItemRequest) = intent {
        if (request.path.isEmpty()) return@intent
        val trimmedTitle = request.title.trim()
        if (trimmedTitle.isBlank()) return@intent
        val normalizedRequest = when (request) {
            is UpdateBookmarkItemRequest.Bookmark -> request.copy(
                title = trimmedTitle,
                description = request.description.trim(),
                tags = normalizeTags(request.tags),
            )

            is UpdateBookmarkItemRequest.Folder -> request.copy(title = trimmedTitle)
        }

        val currentSnapshotId = state.selectedBookmarkId ?: return@intent
        val currentDocument = state.bookmarkSnapshots[currentSnapshotId] ?: return@intent
        val savedSnapshotId = bookmarkSnapshotEditor.updateItem(
            snapshotId = currentSnapshotId,
            document = currentDocument,
            request = normalizedRequest,
        ) ?: return@intent
        reduce { state.copy(selectedBookmarkId = savedSnapshotId) }
    }

    fun renameBookmarkSnapshot(snapshotId: SnapshotId, title: String) = intent {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) return@intent

        val currentDocument = state.bookmarkSnapshots[snapshotId] ?: return@intent
        val savedSnapshotId = bookmarkSnapshotEditor.renameSnapshot(
            snapshotId = snapshotId,
            document = currentDocument,
            title = trimmedTitle,
        )
        reduce { state.copy(selectedBookmarkId = savedSnapshotId) }
    }

    private fun normalizeUrl(url: String): String {
        val trimmedUrl = url.trim()
        return if (trimmedUrl.contains("://")) trimmedUrl else "https://$trimmedUrl"
    }

    private fun normalizeTags(tags: List<String>): List<String> = tags
        .map(String::trim)
        .filter(String::isNotBlank)
        .distinct()

    private fun List<String>.reorderInboxFirst(
        bookmarkDocuments: Map<String, com.hdw.bookmarker.core.model.bookmark.BookmarkDocument>,
    ): List<String> {
        val (inboxIds, otherIds) = partition { id -> bookmarkDocuments[id]?.isInboxSnapshot() == true }
        return inboxIds + otherIds
    }

    private fun currentEpochSecondsString(): String = (System.currentTimeMillis() / 1000L).toString()

    private fun String?.toBookmarkDisplayType(): BookmarkDisplayType = when (this) {
        BookmarkDisplayType.ICON.name -> BookmarkDisplayType.ICON
        BookmarkDisplayType.LIST.name -> BookmarkDisplayType.LIST
        else -> BookmarkDisplayType.LIST
    }
}
