package com.hdw.bookmarker.core.ui.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showShortToast(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showShortToast(@StringRes messageResId: Int) {
    Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
}
