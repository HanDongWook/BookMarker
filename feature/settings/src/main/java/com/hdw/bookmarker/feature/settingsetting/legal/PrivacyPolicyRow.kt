package com.hdw.bookmarker.feature.settingsetting.legal

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settingsetting.SettingsRow

@Composable
fun PrivacyPolicyRow(onClick: () -> Unit) {
    SettingsRow(
        title = stringResource(R.string.privacy_policy_label),
        onClick = onClick,
    )
}
