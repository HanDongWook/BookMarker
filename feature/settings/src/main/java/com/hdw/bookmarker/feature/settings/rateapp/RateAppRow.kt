package com.hdw.bookmarker.feature.settings.rateapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.SettingsRow

@Composable
fun RateAppRow(onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.rate_app_label),
        onClick = onClick,
    )
}
