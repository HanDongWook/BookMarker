package com.hdw.bookmarker.core.ui.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.net.toUri
import com.hdw.bookmarker.core.common.uri.AppUri

data class InstalledBrowserInfo(val packageName: String, val appName: String)

fun Context.getInstalledBrowsers(): List<InstalledBrowserInfo> {
    val browserIntent = Intent(Intent.ACTION_VIEW, AppUri.BROWSER_DISCOVERY_URL.toUri()).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
    }

    return packageManager.queryIntentActivities(browserIntent, PackageManager.MATCH_ALL)
        .mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            runCatching {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                InstalledBrowserInfo(packageName = packageName, appName = appName)
            }.getOrNull()
        }
        .distinctBy { it.packageName }
        .sortedBy { it.appName.lowercase() }
}
