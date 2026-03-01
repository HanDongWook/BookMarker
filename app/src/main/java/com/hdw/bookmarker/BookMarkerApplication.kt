package com.hdw.bookmarker

import android.app.Application
import com.airbnb.mvrx.Mavericks
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BookMarkerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initFirebase()
        Mavericks.initialize(this)
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
        FirebasePerformance.getInstance().isPerformanceCollectionEnabled = !BuildConfig.DEBUG
    }
}
