package com.hdw.bookmarker.development

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.android.showkase.ui.ShowkaseBrowserActivity
import com.hdw.bookmarker.R
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerTheme
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.development.di.BookMarkerShowkaseRootModule
import com.hdw.bookmarker.development.presentation.DevelopmentScreen
import com.hdw.bookmarker.feature.settings.presentation.component.SettingsRow
import com.hdw.bookmarker.main.MainActivity

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
