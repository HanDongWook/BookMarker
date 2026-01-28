package com.hdw.bookmarker.util.toast

import android.content.Context
import androidx.annotation.StringRes

fun Context.showToast(message: String, long: Boolean = false) {
    if (long) {
        ToastHelper.showLong(this, message)
    } else {
        ToastHelper.showShort(this, message)
    }
}

fun Context.showToast(@StringRes messageResId: Int, long: Boolean = false) {
    if (long) {
        ToastHelper.showLong(this, messageResId)
    } else {
        ToastHelper.showShort(this, messageResId)
    }
}
