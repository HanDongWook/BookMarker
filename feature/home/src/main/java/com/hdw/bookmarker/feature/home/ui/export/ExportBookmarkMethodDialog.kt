package com.hdw.bookmarker.feature.home.ui.export

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hdw.bookmarker.core.ui.R

@Composable
fun ExportBookmarkMethodDialog(
    onDismiss: () -> Unit,
    onShareTextClick: () -> Unit,
    onShareHtmlClick: () -> Unit,
    onSaveTextClick: () -> Unit,
    onSaveHtmlClick: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.export_bookmark_method_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                ExportMethodSection(
                    title = stringResource(R.string.export_bookmark_method_share_section),
                    options = listOf(
                        ExportMethodOptionData(
                            imageVector = Icons.AutoMirrored.Filled.TextSnippet,
                            title = stringResource(R.string.export_bookmark_method_share_text),
                            onClick = onShareTextClick,
                        ),
                        ExportMethodOptionData(
                            imageVector = Icons.Default.Code,
                            title = stringResource(R.string.export_bookmark_method_share_html),
                            onClick = onShareHtmlClick,
                        ),
                    ),
                )
                ExportMethodSection(
                    title = stringResource(R.string.export_bookmark_method_save_section),
                    options = listOf(
                        ExportMethodOptionData(
                            imageVector = Icons.AutoMirrored.Filled.TextSnippet,
                            title = stringResource(R.string.export_bookmark_method_save_text),
                            onClick = onSaveTextClick,
                        ),
                        ExportMethodOptionData(
                            imageVector = Icons.Default.Code,
                            title = stringResource(R.string.export_bookmark_method_save_html),
                            onClick = onSaveHtmlClick,
                        ),
                    ),
                )
            }
        }
    }
}

@Composable
private fun ExportMethodSection(
    title: String,
    options: List<ExportMethodOptionData>,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 1.dp,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                options.forEach { option ->
                    ExportMethodOption(
                        imageVector = option.imageVector,
                        title = option.title,
                        contentDescription = option.title,
                        onClick = option.onClick,
                    )
                }
            }
        }
    }
}

private data class ExportMethodOptionData(
    val imageVector: ImageVector,
    val title: String,
    val onClick: () -> Unit,
)

@Composable
private fun ExportMethodOption(
    imageVector: ImageVector,
    title: String,
    contentDescription: String,
    onClick: () -> Unit,
) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        leadingContent = {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp),
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    )
}

@Preview(showBackground = true)
@Composable
private fun ExportBookmarkMethodDialogPreview() {
    ExportBookmarkMethodDialog(
        onDismiss = {},
        onShareTextClick = {},
        onShareHtmlClick = {},
        onSaveTextClick = {},
        onSaveHtmlClick = {},
    )
}
