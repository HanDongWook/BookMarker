package com.hdw.bookmarker.core.ui.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import java.util.Locale

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
                if (isExcludedNaverApp(packageName = packageName, appName = appName)) {
                    return@mapNotNull null
                }
                InstalledBrowserInfo(packageName = packageName, appName = appName)
            }.getOrNull()
        }
        .distinctBy { it.packageName }
        .sortedBy { it.appName.lowercase() }
}

private fun isExcludedNaverApp(packageName: String, appName: String): Boolean {
    val normalizedPackage = packageName.lowercase(Locale.ROOT)
    val normalizedName = appName.lowercase(Locale.ROOT)
    val isNaver = normalizedPackage.contains("naver") || normalizedName.contains("naver") || normalizedName.contains("네이버")
    val isWhale = normalizedPackage.contains("whale") || normalizedName.contains("whale") || normalizedName.contains("웨일")
    return isNaver && !isWhale
}
