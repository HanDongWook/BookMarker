package com.hdw.bookmarker.feature.settings.presentation.component.tab.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.presentation.component.SettingsRow

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

@Preview(showBackground = true)
@Composable
private fun ThemeModeRowPreview() {
    MaterialTheme {
        ThemeModeRow(
            selectedThemeMode = SettingsRepository.APP_THEME_MODE_DARK,
            onClick = {},
        )
    }
}
