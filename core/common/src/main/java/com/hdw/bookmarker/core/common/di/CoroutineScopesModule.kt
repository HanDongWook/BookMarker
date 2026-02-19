package com.hdw.bookmarker.core.common.di

import com.hdw.bookmarker.core.common.BookmarkerDispatchers
import com.hdw.bookmarker.core.common.Dispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationIOScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationDefaultScope

@Module
@InstallIn(SingletonComponent::class)
internal object CoroutineScopesModule {

    @Provides
    @Singleton
    @ApplicationIOScope
    fun providesCoroutineIOScope(
        @Dispatcher(BookmarkerDispatchers.IO) dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)

    @Provides
    @Singleton
    @ApplicationDefaultScope
    fun providesCoroutineDefaultScope(
        @Dispatcher(BookmarkerDispatchers.Default) dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}
