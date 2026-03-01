package com.hdw.bookmarker.feature.settingsetting.opensource

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settingsetting.SettingsRow

@Composable
fun OpenSourceLicenseRow(onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.open_source_licenses_label),
        onClick = onClick,
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        },
    )
}
