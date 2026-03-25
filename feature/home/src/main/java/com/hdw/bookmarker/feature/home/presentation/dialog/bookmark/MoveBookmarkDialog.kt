package com.hdw.bookmarker.feature.home.presentation.dialog.bookmark

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.core.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoveBookmarkDialog(
    targets: List<Pair<SnapshotId, String>>,
    onDismiss: () -> Unit,
    onTargetClick: (SnapshotId) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedTargetId by remember(targets) { mutableStateOf(targets.firstOrNull()?.first) }
    val selectedTargetTitle = targets
        .firstOrNull { (snapshotId, _) -> snapshotId == selectedTargetId }
        ?.second
        .orEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.bookmark_item_action_move)) },
        text = {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    value = selectedTargetTitle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = stringResource(R.string.bookmark_item_action_move)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    targets.forEach { (snapshotId, title) ->
                        DropdownMenuItem(
                            text = { Text(text = title) },
                            onClick = {
                                selectedTargetId = snapshotId
                                expanded = false
                            },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedTargetId?.let(onTargetClick)
                },
                enabled = selectedTargetId != null,
            ) {
                Text(text = stringResource(R.string.action_apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.clear_temporary_data_dialog_cancel))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun MoveBookmarkDialogPreview() {
    MoveBookmarkDialog(
        targets = listOf(
            SnapshotId("1") to "Bookmarks 1",
            SnapshotId("2") to "Bookmarks 2",
        ),
        onDismiss = {},
        onTargetClick = {},
    )
}
