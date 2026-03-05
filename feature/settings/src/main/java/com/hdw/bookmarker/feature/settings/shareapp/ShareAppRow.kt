package com.hdw.bookmarker.feature.settings.shareapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.SettingsRow

@Composable
fun ShareAppRow(onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.share_app_label),
        onClick = onClick,
    )
}
