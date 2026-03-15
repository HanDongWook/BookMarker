package com.hdw.bookmarker.feature.settings.presentation.component.tab.defaultbrowser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.presentation.component.SettingsRow

@Composable
fun DefaultBrowserRow(browserName: String, browserIcon: Any?, onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.default_browser_label),
        onClick = onClick,
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (browserIcon != null) {
                    AsyncImage(
                        model = browserIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = browserName,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
internal fun DefaultBrowserRowPreview() {
    MaterialTheme {
        DefaultBrowserRow(
            browserName = "Chrome",
            browserIcon = null,
            onClick = {},
        )
    }
}
