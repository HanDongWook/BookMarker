package com.hdw.bookmarker.feature.home.sharebookmark

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.ui.R

@Composable
fun ShareBookmarkMethodDialog(onDismiss: () -> Unit, onShareTextClick: () -> Unit, onShareHtmlClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.share_bookmark_method_title)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(
                    onClick = onShareTextClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.share_bookmark_method_text))
                }
                TextButton(
                    onClick = onShareHtmlClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.share_bookmark_method_html))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun ShareBookmarkMethodDialogPreview() {
    ShareBookmarkMethodDialog(
        onDismiss = {},
        onShareTextClick = {},
        onShareHtmlClick = {},
    )
}
