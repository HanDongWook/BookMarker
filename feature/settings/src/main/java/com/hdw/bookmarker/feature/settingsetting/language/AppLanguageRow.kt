package com.hdw.bookmarker.feature.settingsetting.language

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settingsetting.SettingsRow

@Composable
fun AppLanguageRow(languageLabel: String, onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.app_language_label),
        value = languageLabel,
        onClick = onClick,
    )
}
