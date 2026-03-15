package com.hdw.bookmarker.feature.settings.presentation.component.tab.temporarydata

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.presentation.component.SettingsRow
import com.hdw.bookmarker.feature.settings.presentation.model.DisplayValueState

@Composable
fun TemporaryDataRow(size: DisplayValueState, onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.temporary_data_label),
        value = when (size) {
            DisplayValueState.Loading -> stringResource(R.string.display_value_loading)
            is DisplayValueState.Loaded -> size.value
            DisplayValueState.Unavailable -> null
        },
        onClick = onClick,
    )
}

@Preview(showBackground = true)
@Composable
internal fun TemporaryDataRowPreview() {
    MaterialTheme {
        TemporaryDataRow(
            size = DisplayValueState.Loaded("12.3 MB"),
            onClick = {},
        )
    }
}
