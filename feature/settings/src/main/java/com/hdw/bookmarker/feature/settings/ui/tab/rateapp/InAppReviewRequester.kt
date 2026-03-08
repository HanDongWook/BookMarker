package com.hdw.bookmarker.feature.settings.ui.tab.rateapp

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.url.AppWebUrl
import com.hdw.bookmarker.core.ui.util.findActivity
import com.hdw.bookmarker.core.ui.util.showShortToast
import kotlinx.coroutines.launch
import timber.log.Timber

internal fun requestInAppReview(context: Context) {
    val activity = context.findActivity()
    if (activity == null) {
        openPlayStoreRatingPage(context)
        return
    }

    val manager = ReviewManagerFactory.create(activity)
    activity.lifecycleScope.launch {
        // In-app review UI visibility is controlled by Google Play quota/policy.
        // Even if request/launch succeeds, the review dialog may not appear repeatedly.
        val reviewInfo = runCatching {
            manager.requestReview()
        }.onFailure { throwable ->
            Timber.e(throwable, "Failed to request in-app review")
        }.getOrNull()
        if (reviewInfo == null) {
            openPlayStoreRatingPage(context)
            return@launch
        }

        val didLaunch = runCatching {
            manager.launchReview(activity, reviewInfo)
        }.onFailure { throwable ->
            Timber.e(throwable, "Failed to launch in-app review")
        }.isSuccess
        if (!didLaunch) {
            openPlayStoreRatingPage(context)
            return@launch
        }
        context.showShortToast(R.string.rate_app_review_requested_hint)
    }
}

private fun openPlayStoreRatingPage(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, AppWebUrl.PLAY_STORE_URL.toUri()).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching {
        context.startActivity(intent)
    }.onFailure { throwable ->
        Timber.e(throwable, "Failed to open Play Store for rating")
        context.showShortToast(R.string.rate_app_open_store_failed)
    }
}
