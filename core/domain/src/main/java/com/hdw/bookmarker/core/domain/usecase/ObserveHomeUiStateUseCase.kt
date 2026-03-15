package com.hdw.bookmarker.core.domain.usecase

import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.model.home.HomeObservedState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveHomeUiStateUseCase @Inject constructor(
    private val getBookmarkSnapshotsUseCase: GetBookmarkSnapshotsUseCase,
    private val getOrderedSnapshotIdsUseCase: GetOrderedSnapshotIdsUseCase,
    private val getBookmarkColorsUseCase: GetBookmarkColorsUseCase,
    private val getDefaultBrowserPackageUseCase: GetDefaultBrowserPackageUseCase,
    private val getBookmarkDisplayTypeUseCase: GetBookmarkDisplayTypeUseCase,
    private val getScrollLongBookmarkUrlUseCase: GetScrollLongBookmarkUrlUseCase,
    private val getBookmarkSecondaryDisplayTypeUseCase: GetBookmarkSecondaryDisplayTypeUseCase,
    private val getOpenBookmarkAdjacentOnLargeScreenUseCase: GetOpenBookmarkAdjacentOnLargeScreenUseCase,
    private val getOpenBookmarkSidePreviewOnLargeScreenUseCase: GetOpenBookmarkSidePreviewOnLargeScreenUseCase,
    private val getShowFolderDescriptionUseCase: GetShowFolderDescriptionUseCase,
    private val getScrollLongFolderDescriptionUseCase: GetScrollLongFolderDescriptionUseCase,
    private val getBookmarkFolderIconStyleUseCase: GetBookmarkFolderIconStyleUseCase,
) {
    operator fun invoke(): Flow<HomeObservedState> {
        val bookmarkData = combine(
            getBookmarkSnapshotsUseCase(),
            getOrderedSnapshotIdsUseCase(),
            getBookmarkColorsUseCase(),
        ) { bookmarkDocuments, orderedSnapshotIds, bookmarkColors ->
            BookmarkData(
                bookmarkDocuments = bookmarkDocuments,
                orderedSnapshotIds = orderedSnapshotIds,
                bookmarkColors = bookmarkColors,
            )
        }

        val openingPreferences = combine(
            getDefaultBrowserPackageUseCase().map { it?.packageName },
            getOpenBookmarkAdjacentOnLargeScreenUseCase(),
            getOpenBookmarkSidePreviewOnLargeScreenUseCase(),
        ) { defaultBrowserPackage, openBookmarkAdjacentOnLargeScreen, openBookmarkSidePreviewOnLargeScreen ->
            OpeningPreferences(
                defaultBrowserPackage = defaultBrowserPackage,
                openBookmarkAdjacentOnLargeScreen = openBookmarkAdjacentOnLargeScreen,
                openBookmarkSidePreviewOnLargeScreen = openBookmarkSidePreviewOnLargeScreen,
            )
        }

        val displayPreferences = combine(
            getScrollLongBookmarkUrlUseCase(),
            getBookmarkSecondaryDisplayTypeUseCase(),
            getBookmarkDisplayTypeUseCase(),
        ) { scrollLongBookmarkUrl, bookmarkSecondaryDisplayType, bookmarkDisplayType ->
            BookmarkDisplayPreferences(
                bookmarkDisplayType = bookmarkDisplayType,
                scrollLongBookmarkUrl = scrollLongBookmarkUrl,
                bookmarkSecondaryDisplayType = bookmarkSecondaryDisplayType,
            )
        }

        val folderPreferences = combine(
            getShowFolderDescriptionUseCase(),
            getScrollLongFolderDescriptionUseCase(),
            getBookmarkFolderIconStyleUseCase(),
        ) { showFolderDescription, scrollLongFolderDescription, folderIconStyle ->
            FolderDisplayPreferences(
                showFolderDescription = showFolderDescription,
                scrollLongFolderDescription = scrollLongFolderDescription,
                folderIconStyle = folderIconStyle,
            )
        }

        return combine(
            bookmarkData,
            openingPreferences,
            displayPreferences,
            folderPreferences,
        ) { bookmarkDataState, openingPreferencesState, bookmarkDisplayPreferences, folderDisplayPreferences ->
            HomeObservedState(
                orderedSnapshotIds = bookmarkDataState.orderedSnapshotIds,
                bookmarkDocuments = bookmarkDataState.bookmarkDocuments,
                bookmarkColors = bookmarkDataState.bookmarkColors,
                defaultBrowserPackage = openingPreferencesState.defaultBrowserPackage,
                bookmarkDisplayType = bookmarkDisplayPreferences.bookmarkDisplayType,
                scrollLongBookmarkUrl = bookmarkDisplayPreferences.scrollLongBookmarkUrl,
                bookmarkSecondaryDisplayType = bookmarkDisplayPreferences.bookmarkSecondaryDisplayType,
                openBookmarkAdjacentOnLargeScreen =
                openingPreferencesState.openBookmarkAdjacentOnLargeScreen,
                openBookmarkSidePreviewOnLargeScreen =
                openingPreferencesState.openBookmarkSidePreviewOnLargeScreen,
                showFolderDescription = folderDisplayPreferences.showFolderDescription,
                scrollLongFolderDescription = folderDisplayPreferences.scrollLongFolderDescription,
                folderIconStyle = folderDisplayPreferences.folderIconStyle,
            )
        }
    }

    private data class BookmarkData(
        val bookmarkDocuments: Map<String, BookmarkDocument>,
        val orderedSnapshotIds: List<String>,
        val bookmarkColors: Map<String, Long>,
    )

    private data class OpeningPreferences(
        val defaultBrowserPackage: String?,
        val openBookmarkAdjacentOnLargeScreen: Boolean,
        val openBookmarkSidePreviewOnLargeScreen: Boolean,
    )

    private data class BookmarkDisplayPreferences(
        val bookmarkDisplayType: String?,
        val scrollLongBookmarkUrl: Boolean,
        val bookmarkSecondaryDisplayType: String?,
    )

    private data class FolderDisplayPreferences(
        val showFolderDescription: Boolean,
        val scrollLongFolderDescription: Boolean,
        val folderIconStyle: BookmarkFolderIconStyle,
    )
}
