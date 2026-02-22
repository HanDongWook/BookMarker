package com.hdw.bookmarker.feature.home.dialog

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.hdw.bookmarker.feature.home.R

@Composable
fun BookmarkImportGuideDialog(
    icon: Drawable?,
    onDismiss: () -> Unit,
    onOpenDesktopGuide: () -> Unit,
    onSelectFile: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (icon != null) {
                        Image(
                            painter = rememberDrawablePainter(drawable = icon),
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(android.R.string.cancel))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.import_guide_title),
                    style = MaterialTheme.typography.headlineSmall,
                )

                Spacer(modifier = Modifier.height(16.dp))

                GuideSection(
                    title = stringResource(R.string.import_guide_step1_title),
                    descriptions = arrayOf(
                        stringResource(R.string.import_guide_step1_body_notice),
                        stringResource(R.string.import_guide_step1_body_desktop),
                    ),
                    buttonText = stringResource(R.string.import_guide_step1_button),
                    onClick = onOpenDesktopGuide,
                )

                Spacer(modifier = Modifier.height(12.dp))

                GuideSection(
                    title = stringResource(R.string.import_guide_step2_title),
                )

                Spacer(modifier = Modifier.height(12.dp))

                GuideSection(
                    title = stringResource(R.string.import_guide_step3_title),
                    descriptions = arrayOf(stringResource(R.string.import_guide_step3_body)),
                    buttonText = stringResource(R.string.import_guide_step3_button),
                    onClick = onSelectFile,
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun GuideSection(
    title: String,
    vararg descriptions: String,
    buttonText: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            descriptions.forEach { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (buttonText != null && onClick != null) {
                Button(onClick = onClick) {
                    Text(text = buttonText)
                }
            }
        }
    }
}
