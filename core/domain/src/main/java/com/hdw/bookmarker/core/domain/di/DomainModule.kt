package com.hdw.bookmarker.core.domain.di

import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import com.hdw.bookmarker.core.data.repository.BrowserRepository
import com.hdw.bookmarker.core.domain.usecase.GetBookmarksUseCase
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
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
