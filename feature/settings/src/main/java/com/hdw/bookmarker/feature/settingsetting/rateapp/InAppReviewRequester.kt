package com.hdw.bookmarker.feature.settingsetting.rateapp

import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import com.hdw.bookmarker.core.ui.util.findActivity
import timber.log.Timber

internal fun requestInAppReview(context: Context) {
    val activity = context.findActivity() ?: return
    val manager = ReviewManagerFactory.create(activity)
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        Timber.e("requestInAppReview ${task.isSuccessful} ${task.exception}")
        val reviewInfo = task.result ?: return@addOnCompleteListener
        manager.launchReviewFlow(activity, reviewInfo)
    }
}
