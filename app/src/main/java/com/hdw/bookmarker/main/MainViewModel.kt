package com.hdw.bookmarker.main

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import BrowserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject
import androidx.core.net.toUri

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    /**
     * 디바이스에 설치된 브라우저 목록을 가져옵니다.
     * @return 설치된 브라우저 정보 리스트
     */
    fun getInstalledBrowsers(): List<BrowserInfo> {
        val packageManager = application.packageManager

        val browserIntent = Intent(Intent.ACTION_VIEW, "https://www.example.com".toUri()).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }
        val resolveInfoList = packageManager.queryIntentActivities(
            browserIntent,
            PackageManager.MATCH_ALL
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
                    icon = icon
                )
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.e(e, "Failed to get browser info")
                null
            }
        }.distinctBy { it.packageName }

        Timber.d("Found ${browsers.size} browsers: ${browsers.map { it.appName }}")
        return browsers
    }
}