package com.hdw.bookmarker.feature.importguide.ui.picker

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import com.hdw.bookmarker.core.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserPickerScreen(
    installedBrowsers: List<BrowserInfo>,
    onOpenDesktopGuide: (String) -> Unit,
    onBackClick: () -> Unit,
    iconModifierForBrowser: @Composable (BrowserInfo) -> Modifier = { Modifier },
    textModifierForBrowser: @Composable (BrowserInfo) -> Modifier = { Modifier },
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.installed_browser)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.bookmark_icon_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            installedBrowsers.forEach { browser ->
                BrowserItem(
                    browser = browser,
                    onSyncClick = { onOpenDesktopGuide(browser.packageName) },
                    iconModifier = iconModifierForBrowser(browser),
                    textModifier = textModifierForBrowser(browser),
                )
            }
        }
    }
}

@Composable
private fun BrowserItem(
    browser: BrowserInfo,
    onSyncClick: () -> Unit,
    iconModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onSyncClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = rememberDrawablePainter(drawable = browser.icon),
            contentDescription = browser.appName,
            modifier = iconModifier.size(40.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = browser.appName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = textModifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowRight,
            contentDescription = stringResource(R.string.sync),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BrowserItemPreview() {
    BrowserItem(
        browser = BrowserInfo(
            packageName = "com.android.chrome",
            appName = "Chrome",
            icon = ColorDrawable(Color.GRAY),
        ),
        onSyncClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BrowserPickerScreenPreview() {
    BrowserPickerScreen(
        installedBrowsers = listOf(
            BrowserInfo(
                packageName = "com.android.chrome",
                appName = "Chrome",
                icon = ColorDrawable(Color.GRAY),
            ),
            BrowserInfo(
                packageName = "com.microsoft.emmx",
                appName = "Edge",
                icon = ColorDrawable(Color.LTGRAY),
            ),
        ),
        onOpenDesktopGuide = {},
        onBackClick = {},
    )
}
