package com.hdw.bookmarker.feature.settingsetting.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settingsetting.SettingsRow

@Composable
fun ThemeModeRow(selectedThemeMode: String, onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.theme_label),
        value = if (selectedThemeMode == SettingsRepository.APP_THEME_MODE_DARK) {
            stringResource(R.string.theme_dark)
        } else {
            stringResource(R.string.theme_light)
        },
        onClick = onClick,
    )
}
