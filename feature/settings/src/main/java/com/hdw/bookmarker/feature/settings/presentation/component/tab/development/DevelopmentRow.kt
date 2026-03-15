package com.hdw.bookmarker.feature.settings.presentation.component.tab.development

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.presentation.component.SettingsRow

@Composable
fun DevelopmentRow(onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.development_label),
        onClick = onClick,
    )
}

@Preview(showBackground = true)
@Composable
internal fun DevelopmentRowPreview() {
    MaterialTheme {
        DevelopmentRow(onClick = {})
    }
}
