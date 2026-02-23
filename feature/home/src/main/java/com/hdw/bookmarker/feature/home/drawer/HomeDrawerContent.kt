package com.hdw.bookmarker.feature.home.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import com.hdw.bookmarker.feature.home.R

@Composable
fun HomeDrawerContent(
    installedBrowsers: List<BrowserInfo>,
    connectedBrowserPackages: Set<String>,
    onSyncClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.installed_browser),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        installedBrowsers.forEach { browser ->
            BrowserItem(
                browser = browser,
                isConnected = connectedBrowserPackages.contains(browser.packageName),
                onSyncClick = { onSyncClick(browser.packageName) },
            )
        }
    }
}

@Composable
private fun BrowserItem(browser: BrowserInfo, isConnected: Boolean, onSyncClick: () -> Unit) {
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
            modifier = Modifier
                .size(40.dp)
                .alpha(if (isConnected) 1f else 0.5f),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = browser.appName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Default.Sync,
            contentDescription = stringResource(R.string.sync),
        )
    }
}
