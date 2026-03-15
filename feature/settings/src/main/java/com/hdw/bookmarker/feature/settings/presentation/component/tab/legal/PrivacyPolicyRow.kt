package com.hdw.bookmarker.feature.settings.presentation.component.tab.legal

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.presentation.component.SettingsRow

@Composable
fun PrivacyPolicyRow(onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.privacy_policy_label),
        onClick = onClick,
    )
}

@Preview(showBackground = true)
@Composable
internal fun PrivacyPolicyRowPreview() {
    MaterialTheme {
        PrivacyPolicyRow(onClick = {})
    }
}
