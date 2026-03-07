package com.hdw.bookmarker.feature.settings.legal

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.SettingsRow

@Composable
fun PrivacyPolicyRow(onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.privacy_policy_label),
        onClick = onClick,
    )
}

@Preview(showBackground = true)
@Composable
private fun PrivacyPolicyRowPreview() {
    MaterialTheme {
        PrivacyPolicyRow(onClick = {})
    }
}
