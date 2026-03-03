package com.hdw.bookmarker.feature.settings.temporarydata

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.SettingsRow

@Composable
fun TemporaryDataRow(size: String, onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.temporary_data_label),
        value = size,
        onClick = onClick,
    )
}
