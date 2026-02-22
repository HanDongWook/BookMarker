package com.hdw.bookmarker.core.ui.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

fun Context.getAppVersionName(): String {
    return runCatching {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
        packageInfo.versionName ?: "-"
    }.getOrDefault("-")
}
