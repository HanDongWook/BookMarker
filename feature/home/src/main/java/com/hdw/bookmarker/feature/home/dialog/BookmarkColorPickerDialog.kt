package com.hdw.bookmarker.feature.home.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.ui.R

@Composable
fun BookmarkColorPickerDialog(
    colors: List<Long>,
    currentColor: Long,
    onColorSelect: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.bookmark_color_picker_dialog_title))
        },
        text = {
            val columns = 4
            Column(
                modifier = Modifier.wrapContentWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                colors.chunked(columns).forEach { rowColors ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        rowColors.forEach { colorValue ->
                            val color = Color(colorValue)
                            val isSelected = currentColor == colorValue
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color = color, shape = CircleShape)
                                    .then(
                                        if (isSelected) {
                                            Modifier.border(
                                                width = 2.dp,
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = CircleShape,
                                            )
                                        } else {
                                            Modifier
                                        },
                                    )
                                    .clickable { onColorSelect(colorValue) },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.bookmark_color_picker_dialog_close))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun BookmarkColorPickerDialogPreview() {
    BookmarkColorPickerDialog(
        colors = listOf(
            0xFFEF5350,
            0xFFFFA726,
            0xFFFFEE58,
            0xFF66BB6A,
            0xFF42A5F5,
            0xFF7E57C2,
            0xFFEC407A,
            0xFF8D6E63,
        ),
        currentColor = 0xFF42A5F5,
        onColorSelect = {},
        onDismiss = {},
    )
}
