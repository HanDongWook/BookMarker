package com.hdw.bookmarker.core.ui.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

data class InstalledBrowserInfo(val packageName: String, val appName: String)

fun Context.getInstalledBrowsers(): List<InstalledBrowserInfo> {
    val browserIntent = Intent.makeMainSelectorActivity(
        Intent.ACTION_MAIN,
        Intent.CATEGORY_APP_BROWSER,
    )

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
