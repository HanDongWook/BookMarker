package com.hdw.bookmarker.feature.home.ui.share
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.export_bookmark_method_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                ExportMethodRow(
                    startOption = ExportMethodOptionData(
                        imageVector = Icons.AutoMirrored.Filled.TextSnippet,
                        title = stringResource(R.string.export_bookmark_method_share_text),
                        onClick = onShareTextClick,
                    ),
                    endOption = ExportMethodOptionData(
                        imageVector = Icons.Default.Code,
                        title = stringResource(R.string.export_bookmark_method_share_html),
                        onClick = onShareHtmlClick,
                    ),
                )
                ExportMethodRow(
                    startOption = ExportMethodOptionData(
                        imageVector = Icons.Default.SaveAlt,
                        title = stringResource(R.string.export_bookmark_method_save_text),
                        onClick = onSaveTextClick,
                    ),
                    endOption = ExportMethodOptionData(
                        imageVector = Icons.Default.SaveAlt,
                        title = stringResource(R.string.export_bookmark_method_save_html),
                        onClick = onSaveHtmlClick,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ExportMethodRow(
    startOption: ExportMethodOptionData,
    endOption: ExportMethodOptionData,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        ExportMethodOption(
            modifier = Modifier.weight(1f),
            imageVector = startOption.imageVector,
            title = startOption.title,
            contentDescription = startOption.title,
            onClick = startOption.onClick,
        )
        ExportMethodOption(
            modifier = Modifier.weight(1f),
            imageVector = endOption.imageVector,
            title = endOption.title,
            contentDescription = endOption.title,
            onClick = endOption.onClick,
        )
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
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(40.dp),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
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
