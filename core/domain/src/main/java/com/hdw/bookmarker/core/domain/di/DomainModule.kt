package com.hdw.bookmarker.core.domain.di

import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import com.hdw.bookmarker.core.data.repository.BrowserRepository
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.domain.usecase.ClearBookmarkSnapshotUseCase
import com.hdw.bookmarker.core.domain.usecase.GetAppThemeModeUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkDisplayTypeUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkFolderIconStyleUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkRawFileHashUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkSnapshotRawFileHashUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkSnapshotsUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarksUseCase
import com.hdw.bookmarker.core.domain.usecase.GetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.domain.usecase.GetOrderedSnapshotIdsUseCase
import com.hdw.bookmarker.core.domain.usecase.GetShowBookmarkUrlUseCase
import com.hdw.bookmarker.core.domain.usecase.SaveBookmarkSnapshotUseCase
import com.hdw.bookmarker.core.domain.usecase.SetAppThemeModeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkColorUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkDisplayTypeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkFolderIconColorUseCase
import com.hdw.bookmarker.core.domain.usecase.SetBookmarkFolderIconShapeUseCase
import com.hdw.bookmarker.core.domain.usecase.SetDefaultBrowserPackageUseCase
import com.hdw.bookmarker.core.domain.usecase.SetShowBookmarkUrlUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideGetInstalledBrowsersUseCase(browserRepository: BrowserRepository): GetInstalledBrowsersUseCase =
        GetInstalledBrowsersUseCase(browserRepository)

    @Provides
    @Singleton
    fun provideGetBookmarksUseCase(bookmarkRepository: BookmarkRepository): GetBookmarksUseCase =
        GetBookmarksUseCase(bookmarkRepository)

    @Provides
    @Singleton
    fun provideGetBookmarkRawFileHashUseCase(bookmarkRepository: BookmarkRepository): GetBookmarkRawFileHashUseCase =
        GetBookmarkRawFileHashUseCase(bookmarkRepository)

    @Provides
    @Singleton
    fun provideGetBookmarkSnapshotRawFileHashUseCase(
        bookmarkRepository: BookmarkRepository,
    ): GetBookmarkSnapshotRawFileHashUseCase = GetBookmarkSnapshotRawFileHashUseCase(bookmarkRepository)

    @Provides
    @Singleton
    fun provideGetBookmarkSnapshotsUseCase(bookmarkRepository: BookmarkRepository): GetBookmarkSnapshotsUseCase =
        GetBookmarkSnapshotsUseCase(bookmarkRepository)

    @Provides
    @Singleton
    fun provideGetOrderedSnapshotIdsUseCase(bookmarkRepository: BookmarkRepository): GetOrderedSnapshotIdsUseCase =
        GetOrderedSnapshotIdsUseCase(bookmarkRepository)

    @Provides
    @Singleton
    fun provideSaveBookmarkSnapshotUseCase(bookmarkRepository: BookmarkRepository): SaveBookmarkSnapshotUseCase =
        SaveBookmarkSnapshotUseCase(bookmarkRepository)

    @Provides
    @Singleton
    fun provideClearBookmarkSnapshotUseCase(bookmarkRepository: BookmarkRepository): ClearBookmarkSnapshotUseCase =
        ClearBookmarkSnapshotUseCase(bookmarkRepository)

    @Provides
    @Singleton
    fun provideSetBookmarkColorUseCase(bookmarkRepository: BookmarkRepository): SetBookmarkColorUseCase =
        SetBookmarkColorUseCase(bookmarkRepository)

    @Provides
    @Singleton
    fun provideGetDefaultBrowserPackageUseCase(
        browserRepository: BrowserRepository,
        settingsRepository: SettingsRepository,
    ): GetDefaultBrowserPackageUseCase = GetDefaultBrowserPackageUseCase(
        browserRepository = browserRepository,
        settingsRepository = settingsRepository,
    )

    @Provides
    @Singleton
    fun provideSetDefaultBrowserPackageUseCase(
        settingsRepository: SettingsRepository,
    ): SetDefaultBrowserPackageUseCase = SetDefaultBrowserPackageUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideGetBookmarkDisplayTypeUseCase(settingsRepository: SettingsRepository): GetBookmarkDisplayTypeUseCase =
        GetBookmarkDisplayTypeUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideSetBookmarkDisplayTypeUseCase(settingsRepository: SettingsRepository): SetBookmarkDisplayTypeUseCase =
        SetBookmarkDisplayTypeUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideGetShowBookmarkUrlUseCase(settingsRepository: SettingsRepository): GetShowBookmarkUrlUseCase =
        GetShowBookmarkUrlUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideSetShowBookmarkUrlUseCase(settingsRepository: SettingsRepository): SetShowBookmarkUrlUseCase =
        SetShowBookmarkUrlUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideSetBookmarkFolderIconShapeUseCase(
        settingsRepository: SettingsRepository,
    ): SetBookmarkFolderIconShapeUseCase = SetBookmarkFolderIconShapeUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideGetBookmarkFolderIconStyleUseCase(
        settingsRepository: SettingsRepository,
    ): GetBookmarkFolderIconStyleUseCase = GetBookmarkFolderIconStyleUseCase(
        settingsRepository = settingsRepository,
    )

    @Provides
    @Singleton
    fun provideSetBookmarkFolderIconColorUseCase(
        settingsRepository: SettingsRepository,
    ): SetBookmarkFolderIconColorUseCase = SetBookmarkFolderIconColorUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideGetAppThemeModeUseCase(settingsRepository: SettingsRepository): GetAppThemeModeUseCase =
        GetAppThemeModeUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideSetAppThemeModeUseCase(settingsRepository: SettingsRepository): SetAppThemeModeUseCase =
        SetAppThemeModeUseCase(settingsRepository)
}
