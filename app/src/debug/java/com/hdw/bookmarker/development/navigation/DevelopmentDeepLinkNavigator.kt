package com.hdw.bookmarker.development.navigation

import android.content.Context
import android.content.Intent
import com.hdw.bookmarker.main.AppNavigationDeepLink
import com.hdw.bookmarker.main.MainActivity

internal object DevelopmentDeepLinkNavigator {
    fun openMain(context: Context, target: AppNavigationDeepLink.Target) {
        val deepLinkUri = AppNavigationDeepLink.toUri(target)
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri, context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        context.startActivity(intent)
    }
}
