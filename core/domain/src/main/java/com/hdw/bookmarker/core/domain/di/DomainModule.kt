package com.hdw.bookmarker.core.domain.di

import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import com.hdw.bookmarker.core.data.repository.BrowserRepository
import com.hdw.bookmarker.core.domain.usecase.ClearBookmarkSnapshotUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkRawFileHashUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkSnapshotRawFileHashUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarkSnapshotsUseCase
import com.hdw.bookmarker.core.domain.usecase.GetBookmarksUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.domain.usecase.SaveBookmarkSnapshotUseCase
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
    fun provideSaveBookmarkSnapshotUseCase(bookmarkRepository: BookmarkRepository): SaveBookmarkSnapshotUseCase =
        SaveBookmarkSnapshotUseCase(bookmarkRepository)

    @Provides
    @Singleton
    fun provideClearBookmarkSnapshotUseCase(bookmarkRepository: BookmarkRepository): ClearBookmarkSnapshotUseCase =
        ClearBookmarkSnapshotUseCase(bookmarkRepository)
}
