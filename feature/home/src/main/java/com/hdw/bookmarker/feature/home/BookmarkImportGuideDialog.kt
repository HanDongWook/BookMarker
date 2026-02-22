package com.hdw.bookmarker.feature.home

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun BookmarkImportGuideDialog(icon: Drawable?, onDismiss: () -> Unit, onSelectFile: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            if (icon != null) {
                Image(
                    painter = rememberDrawablePainter(drawable = icon),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                )
            }
        },
        title = { Text(text = stringResource(R.string.home_import_guide_title)) },
        text = { Text(text = stringResource(R.string.home_import_guide_body)) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
        confirmButton = {
            Button(onClick = onSelectFile) {
                Text(text = stringResource(R.string.home_import_guide_select_file))
            }
        },
    )
}
