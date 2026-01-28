package com.hdw.bookmarker.base

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.hdw.bookmarker.R
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge

abstract class BaseActivity : ComponentActivity() {

    private var backPressedTime: Long = 0
    private val backPressedInterval: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupBackPressedHandler()
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()
                if (currentTime - backPressedTime < backPressedInterval) {
                    finish()
                } else {
                    backPressedTime = currentTime
                    Toast.makeText(
                        this@BaseActivity,
                        R.string.press_back_to_exit,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}
