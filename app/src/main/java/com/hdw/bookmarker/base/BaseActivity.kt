package com.hdw.bookmarker.base

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.showShortToast

abstract class BaseActivity : AppCompatActivity() {

    private var backPressedTime: Long = 0
    private val backPressedInterval: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupBackPressedHandler()
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - backPressedTime < backPressedInterval) {
                        finish()
                    } else {
                        backPressedTime = currentTime
                        this@BaseActivity.showShortToast(R.string.press_back_to_exit)
                    }
                }
            },
        )
    }
}
