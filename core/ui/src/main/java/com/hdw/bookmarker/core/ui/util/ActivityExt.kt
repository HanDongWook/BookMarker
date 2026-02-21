package com.hdw.bookmarker.core.ui.util

import android.app.Activity
import android.content.Intent

const val PICK_FILE_REQUEST_CODE = 10000

fun Activity.startFilePicker(mimeType: String) {
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = mimeType
        addCategory(Intent.CATEGORY_OPENABLE)
    }
    startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
}
