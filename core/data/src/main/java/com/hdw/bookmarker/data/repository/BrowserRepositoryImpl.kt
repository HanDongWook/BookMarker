package com.hdw.bookmarker.data.repository

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.net.toUri
import com.hdw.bookmarker.model.BrowserInfo
import timber.log.Timber
import javax.inject.Inject

class BrowserRepositoryImpl @Inject constructor(private val application: Application) :
    BrowserRepository {

    override fun getInstalledBrowsers(): List<BrowserInfo> {
        val packageManager = application.packageManager

        val browserIntent = Intent(Intent.ACTION_VIEW, "https://www.example.com".toUri()).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }
        val resolveInfoList = packageManager.queryIntentActivities(
            browserIntent,
            PackageManager.MATCH_ALL,
        )

        val browsers = resolveInfoList.mapNotNull { resolveInfo ->
            try {
                val packageName = resolveInfo.activityInfo.packageName
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                val appName = packageManager.getApplicationLabel(appInfo).toString()
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
}
