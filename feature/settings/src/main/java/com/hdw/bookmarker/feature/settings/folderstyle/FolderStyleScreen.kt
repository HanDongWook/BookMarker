package com.hdw.bookmarker.feature.settings.folderstyle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.label
import com.hdw.bookmarker.feature.settings.SettingsRow
import com.hdw.bookmarker.feature.settings.folderstyle.color.FolderColorDialog
import com.hdw.bookmarker.feature.settings.folderstyle.shape.FolderShapeDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderStyleScreen(
    selectedFolderIconStyle: BookmarkFolderIconStyle,
    onBackClick: () -> Unit,
    onShapeSelect: (BookmarkFolderIconShape) -> Unit,
    onColorSelect: (BookmarkFolderIconColor) -> Unit,
) {
    var showShapeDialog by rememberSaveable { mutableStateOf(false) }
    var showColorDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.folder_style_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            SettingsRow(
                title = stringResource(R.string.folder_shape_label),
                value = selectedFolderIconStyle.shape.label(),
                onClick = { showShapeDialog = true },
            )
            BookMarkerDivider()
            SettingsRow(
                title = stringResource(R.string.folder_color_label),
                value = selectedFolderIconStyle.color.label(),
                onClick = { showColorDialog = true },
            )
            BookMarkerDivider()
        }
    }

    if (showShapeDialog) {
        FolderShapeDialog(
            selectedFolderIconStyle = selectedFolderIconStyle,
            onDismiss = { showShapeDialog = false },
            onShapeSelect = {
                onShapeSelect(it)
                showShapeDialog = false
            },
        )
    }

    if (showColorDialog) {
        FolderColorDialog(
            selectedFolderIconStyle = selectedFolderIconStyle,
            onDismiss = { showColorDialog = false },
            onColorSelect = {
                onColorSelect(it)
                showColorDialog = false
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FolderStyleScreenPreview() {
    FolderStyleScreen(
        selectedFolderIconStyle = BookmarkFolderIconStyle(
            shape = BookmarkFolderIconShape.FILLED,
            color = BookmarkFolderIconColor.DEFAULT,
        ),
        onBackClick = {},
        onShapeSelect = {},
        onColorSelect = {},
    )
}
