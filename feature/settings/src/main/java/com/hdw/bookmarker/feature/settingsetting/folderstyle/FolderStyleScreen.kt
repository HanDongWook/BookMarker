package com.hdw.bookmarker.feature.settingsetting.folderstyle

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
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.ui.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.feature.settingsetting.SettingsRow
import com.hdw.bookmarker.feature.settingsetting.folderstyle.color.FolderColorDialog
import com.hdw.bookmarker.feature.settingsetting.folderstyle.shape.FolderShapeDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderStyleScreen(
    selectedShape: BookmarkFolderIconShape,
    selectedColor: BookmarkFolderIconColor,
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
                value = selectedShape.label(),
                onClick = { showShapeDialog = true },
            )
            BookMarkerDivider()
            SettingsRow(
                title = stringResource(R.string.folder_color_label),
                value = selectedColor.label(),
                onClick = { showColorDialog = true },
            )
            BookMarkerDivider()
        }
    }

    if (showShapeDialog) {
        FolderShapeDialog(
            selectedShape = selectedShape,
            selectedColor = selectedColor,
            onDismiss = { showShapeDialog = false },
            onShapeSelect = {
                onShapeSelect(it)
                showShapeDialog = false
            },
        )
    }

    if (showColorDialog) {
        FolderColorDialog(
            selectedShape = selectedShape,
            selectedColor = selectedColor,
            onDismiss = { showColorDialog = false },
            onColorSelect = {
                onColorSelect(it)
                showColorDialog = false
            },
        )
    }
}
