package com.hdw.bookmarker.feature.settings.temporarydata

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.ui.R

@Composable
fun ClearTemporaryDataDialog(onDismiss: () -> Unit, onDelete: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(text = stringResource(R.string.clear_temporary_data_dialog_message))
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.clear_temporary_data_dialog_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text(text = stringResource(R.string.clear_temporary_data_dialog_confirm))
            }
        },
    )
}
