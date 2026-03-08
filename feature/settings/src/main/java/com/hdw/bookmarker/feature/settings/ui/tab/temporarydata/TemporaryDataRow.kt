package com.hdw.bookmarker.feature.settings.ui.tab.temporarydata

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.ui.component.SettingsRow

@Composable
fun TemporaryDataRow(size: String, onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.temporary_data_label),
        value = size,
        onClick = onClick,
    )
}

@Preview(showBackground = true)
@Composable
private fun TemporaryDataRowPreview() {
    MaterialTheme {
        TemporaryDataRow(
            size = "12.3 MB",
            onClick = {},
        )
    }
}
