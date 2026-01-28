package com.hdw.bookmarker.model

import android.graphics.drawable.Drawable

data class BrowserInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?
)