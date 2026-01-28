package com.hdw.bookmarker.initializer

import android.content.Context
import androidx.startup.Initializer
import com.hdw.bookmarker.BuildConfig
import timber.log.Timber

class AppInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
