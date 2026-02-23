package com.hdw.bookmarker.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.hdw.bookmarker.core.model.browser.BrowserInfo

@Composable
fun DefaultBrowserPickerDialog(
    installedBrowsers: List<BrowserInfo>,
    selectedPackage: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text(text = stringResource(R.string.select_default_browser_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                installedBrowsers.forEach { browser ->
                    TextButton(
                        onClick = { onSelect(browser.packageName) },
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = rememberDrawablePainter(drawable = browser.icon),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            Text(
                                text = browser.appName,
                                modifier = Modifier.padding(start = 10.dp),
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            RadioButton(
                                selected = browser.packageName == selectedPackage,
                                onClick = null,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
    )
}
