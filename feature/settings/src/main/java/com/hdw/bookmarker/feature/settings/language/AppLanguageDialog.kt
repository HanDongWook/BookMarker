package com.hdw.bookmarker.feature.settings.language

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.ui.R
import java.util.Locale

@Composable
fun AppLanguageDialog(selectedLanguageTag: String, onDismiss: () -> Unit, onLanguageSelect: (String) -> Unit) {
    var pendingLanguageTag by remember(selectedLanguageTag) { mutableStateOf(selectedLanguageTag) }
    val systemDefaultLabel = stringResource(R.string.app_language_system_default)
    val languageOptions = remember(systemDefaultLabel) {
        buildList {
            add("" to systemDefaultLabel)
            SupportedCountries.supportedLanguageTags.forEach { tag ->
                add(tag to localeDisplayName(tag))
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.app_language_label)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                languageOptions.forEach { (tag, label) ->
                    LanguageOptionRow(
                        label = label,
                        selected = pendingLanguageTag == tag,
                        onClick = { pendingLanguageTag = tag },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onLanguageSelect(pendingLanguageTag) }) {
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

private fun localeDisplayName(tag: String): String {
    val locale = Locale.forLanguageTag(tag)
    val displayName = locale.getDisplayName(locale)
    return displayName.replaceFirstChar { char ->
        if (char.isLowerCase()) {
            char.titlecase(locale)
        } else {
            char.toString()
        }
    }
}

@Composable
private fun LanguageOptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
        )
        RadioButton(selected = selected, onClick = onClick)
    }
}

@Preview(showBackground = true)
@Composable
private fun AppLanguageDialogPreview() {
    MaterialTheme {
        AppLanguageDialog(
            selectedLanguageTag = "",
            onDismiss = {},
            onLanguageSelect = {},
        )
    }
}
