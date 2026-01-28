package com.hdw.bookmarker.initializer

import android.content.Context
import androidx.startup.Initializer
import com.hdw.bookmarker.BuildConfig
import com.hdw.bookmarker.util.logger.TimberDebugTree
import timber.log.Timber

class AppInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(TimberDebugTree())
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
