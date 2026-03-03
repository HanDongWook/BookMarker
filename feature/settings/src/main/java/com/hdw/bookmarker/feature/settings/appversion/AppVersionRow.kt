package com.hdw.bookmarker.feature.settings.appversion

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.SettingsRow

@Composable
fun AppVersionRow(version: String) {
    SettingsRow(
        title = stringResource(R.string.app_version_label),
        value = version,
    )
}
