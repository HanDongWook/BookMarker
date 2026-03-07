package com.hdw.bookmarker.feature.settings.rateapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.SettingsRow

@Composable
fun RateAppRow(onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.rate_app_label),
        onClick = onClick,
    )
}

@Preview(showBackground = true)
@Composable
private fun RateAppRowPreview() {
    MaterialTheme {
        RateAppRow(onClick = {})
    }
}
