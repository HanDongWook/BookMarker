package com.hdw.bookmarker.feature.settings.opensource

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.SettingsRow

@Composable
fun OpenSourceLicenseRow(onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.open_source_licenses_label),
        onClick = onClick,
    )
}
