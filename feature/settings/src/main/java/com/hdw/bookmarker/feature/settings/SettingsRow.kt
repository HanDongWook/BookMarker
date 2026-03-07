package com.hdw.bookmarker.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsRow(
    title: String,
    value: String? = null,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (onClick != null) {
                    it.clickable(onClick = onClick)
                } else {
                    it
                }
            }
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
        when {
            trailingContent != null -> trailingContent()

            value != null -> {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Value")
@Composable
private fun SettingsRowValuePreview() {
    MaterialTheme {
        SettingsRow(
            title = "Setting title",
            value = "Current value",
        )
    }
}

@Preview(showBackground = true, name = "Trailing")
@Composable
private fun SettingsRowTrailingPreview() {
    MaterialTheme {
        SettingsRow(
            title = "Setting title",
            trailingContent = {
                Text(
                    text = "Action",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
        )
    }
}
