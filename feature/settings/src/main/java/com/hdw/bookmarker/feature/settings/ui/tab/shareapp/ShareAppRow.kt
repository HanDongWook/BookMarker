package com.hdw.bookmarker.feature.settings.ui.tab.shareapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.ui.component.SettingsRow

@Composable
fun ShareAppRow(onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.share_app_label),
        onClick = onClick,
    )
}

@Preview(showBackground = true)
@Composable
private fun ShareAppRowPreview() {
    MaterialTheme {
        ShareAppRow(onClick = {})
    }
}
