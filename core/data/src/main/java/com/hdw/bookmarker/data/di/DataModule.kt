package com.hdw.bookmarker.data.di

import com.hdw.bookmarker.data.repository.BookmarkRepository
import com.hdw.bookmarker.data.repository.BookmarkRepositoryImpl
import com.hdw.bookmarker.data.repository.BrowserRepositoryImpl
import com.hdw.bookmarker.data.repository.BrowserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindBrowserRepository(browserRepositoryImpl: BrowserRepositoryImpl): BrowserRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(bookmarkRepository: BookmarkRepositoryImpl): BookmarkRepository
}
