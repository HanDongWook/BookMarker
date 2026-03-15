package com.hdw.bookmarker.feature.settings.presentation.component.tab.language

import android.content.res.Resources
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.presentation.component.SettingsRow
import java.util.Locale

@Composable
fun AppLanguageRow(languageTag: String, onClick: () -> Unit) {
    val resources = LocalResources.current
    SettingsRow(
        title = stringResource(R.string.app_language_label),
        value = languageTag.toReadableLanguageLabel(resources),
        onClick = onClick,
    )
}

private fun String.toReadableLanguageLabel(resources: Resources): String {
    if (isBlank()) {
        return resources.getString(R.string.app_language_system_default)
    }
    val locale = Locale.forLanguageTag(this)
    val displayName = locale.getDisplayName(locale)
    return displayName.replaceFirstChar { ch ->
        if (ch.isLowerCase()) {
            ch.titlecase(locale)
        } else {
            ch.toString()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppLanguageRowPreview() {
    MaterialTheme {
        AppLanguageRow(
            languageTag = "ko-KR",
            onClick = {},
        )
    }
}
