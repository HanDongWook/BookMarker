package com.hdw.bookmarker.feature.settings.shareapp

import android.content.Context
import android.content.Intent
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.url.AppWebUrl
import com.hdw.bookmarker.core.ui.util.findActivity

internal fun requestAppShare(context: Context) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            context.getString(R.string.share_app_message, AppWebUrl.PLAY_STORE_URL),
        )
    }

    val chooser = Intent.createChooser(
        shareIntent,
        context.getString(R.string.share_app_chooser_title),
    )

    context.findActivity()?.startActivity(chooser)
}
