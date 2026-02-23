package com.hdw.bookmarker.core.data.repository

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import timber.log.Timber
import javax.inject.Inject
import java.util.Locale

class BrowserRepositoryImpl @Inject constructor(private val application: Application) : BrowserRepository {

    override fun getInstalledBrowsers(): List<BrowserInfo> {
        val packageManager = application.packageManager

        val browserIntent = Intent.makeMainSelectorActivity(
            Intent.ACTION_MAIN,
            Intent.CATEGORY_APP_BROWSER,
        )
        val resolveInfoList = packageManager.queryIntentActivities(
            browserIntent,
            PackageManager.MATCH_ALL,
        )

        val browsers = resolveInfoList.mapNotNull { resolveInfo ->
            try {
                val packageName = resolveInfo.activityInfo.packageName
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                if (isExcludedNaverApp(packageName = packageName, appName = appName)) {
                    return@mapNotNull null
                }
                val icon = packageManager.getApplicationIcon(packageName)

                BrowserInfo(
                    packageName = packageName,
                    appName = appName,
                    icon = icon,
                )
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.e(e, "Failed to get browser info")
                null
            }
        }.distinctBy { it.packageName }

        Timber.e("Found ${browsers.size} browsers: ${browsers.map { it.appName }}")
        return browsers
    }

    private fun isExcludedNaverApp(packageName: String, appName: String): Boolean {
        val normalizedPackage = packageName.lowercase(Locale.ROOT)
        val normalizedName = appName.lowercase(Locale.ROOT)
        val isNaver = normalizedPackage.contains("naver") || normalizedName.contains("naver") || normalizedName.contains("네이버")
        val isWhale = normalizedPackage.contains("whale") || normalizedName.contains("whale") || normalizedName.contains("웨일")
        return isNaver && !isWhale
    }
}
