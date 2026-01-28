package com.hdw.bookmarker.data.di

import com.hdw.bookmarker.data.repository.BrowserRepositoryImpl
import com.hdw.bookmarker.domain.repository.BrowserRepository
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
    abstract fun bindBrowserRepository(
        browserRepositoryImpl: BrowserRepositoryImpl
    ): BrowserRepository
}
