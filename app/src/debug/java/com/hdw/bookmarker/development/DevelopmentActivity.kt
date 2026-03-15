package com.hdw.bookmarker.development

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.airbnb.android.showkase.ui.ShowkaseBrowserActivity
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerTheme
import com.hdw.bookmarker.development.di.BookMarkerShowkaseRootModule
import com.hdw.bookmarker.development.presentation.DevelopmentScreen

class DevelopmentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookMarkerTheme {
                DevelopmentScreen(
                    onBackClick = ::finish,
                    onOpenShowkaseClick = {
                        startActivity(
                            ShowkaseBrowserActivity.getIntent(
                                this@DevelopmentActivity,
                                BookMarkerShowkaseRootModule::class.java.canonicalName.orEmpty(),
                            ),
                        )
                    },
                )
            }
        }
    }
}
