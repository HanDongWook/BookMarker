package com.hdw.bookmarker.domain.di

import com.hdw.bookmarker.data.repository.BookmarkRepository
import com.hdw.bookmarker.data.repository.BrowserRepository
import com.hdw.bookmarker.domain.usecase.GetBookmarksUseCase
import com.hdw.bookmarker.domain.usecase.GetInstalledBrowsersUseCase
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
}
