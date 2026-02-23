package com.hdw.bookmarker.core.navigation

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.hdw.bookmarker.core.common.uri.AppUri

object ExternalAppNavigator {
    fun openBookmarkUrl(context: Context, url: String, preferredBrowserPackage: String?): Boolean {
        val uri = url.toUri()
        if (!preferredBrowserPackage.isNullOrBlank()) {
            val preferredResult = runCatching {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, uri).apply {
                        `package` = preferredBrowserPackage
                    },
                )
            }
            if (preferredResult.isSuccess) return true
        }
        return runCatching {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    uri,
                ),
            )
        }.isSuccess
    }

    fun openDesktopChromeBookmarkGuide(context: Context): Boolean = runCatching {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                AppUri.CHROME_BOOKMARK_EXPORT_GUIDE_URL.toUri(),
            ),
        )
    }.isSuccess

    fun openDownloads(context: Context): Boolean = runCatching {
        context.startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
    }.isSuccess
}
