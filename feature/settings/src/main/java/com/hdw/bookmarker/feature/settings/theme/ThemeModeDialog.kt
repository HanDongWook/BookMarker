package com.hdw.bookmarker.feature.settings.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.ui.R

@Composable
fun ThemeModeDialog(selectedThemeMode: String, onDismiss: () -> Unit, onThemeModeSelect: (String) -> Unit) {
    var pendingThemeMode by remember(selectedThemeMode) { mutableStateOf(selectedThemeMode) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.theme_label)) },
        text = {
            Column {
                ThemeModeOption(
                    title = stringResource(R.string.theme_light),
                    selected = pendingThemeMode == SettingsRepository.APP_THEME_MODE_LIGHT,
                    onClick = { pendingThemeMode = SettingsRepository.APP_THEME_MODE_LIGHT },
                )
                ThemeModeOption(
                    title = stringResource(R.string.theme_dark),
                    selected = pendingThemeMode == SettingsRepository.APP_THEME_MODE_DARK,
                    onClick = { pendingThemeMode = SettingsRepository.APP_THEME_MODE_DARK },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onThemeModeSelect(pendingThemeMode) }) {
                Text(text = stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
    )
}

@Composable
private fun ThemeModeOption(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
        )
        RadioButton(selected = selected, onClick = onClick)
    }
}
