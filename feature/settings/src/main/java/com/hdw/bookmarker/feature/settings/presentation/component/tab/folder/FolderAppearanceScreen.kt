package com.hdw.bookmarker.feature.settings.presentation.component.tab.folder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.iconVector
import com.hdw.bookmarker.core.ui.folderstyle.label
import com.hdw.bookmarker.core.ui.folderstyle.resolveTint
import com.hdw.bookmarker.feature.settings.presentation.component.SettingsRow
import com.hdw.bookmarker.feature.settings.presentation.component.tab.folder.color.FolderColorDialog
import com.hdw.bookmarker.feature.settings.presentation.component.tab.folder.shape.FolderShapeDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderAppearanceScreen(
    selectedFolderIconStyle: BookmarkFolderIconStyle,
    showFolderDescription: Boolean,
    scrollLongFolderDescription: Boolean,
    onBackClick: () -> Unit,
    onShapeSelect: (BookmarkFolderIconShape) -> Unit,
    onColorSelect: (BookmarkFolderIconColor) -> Unit,
    onShowFolderDescriptionChange: (Boolean) -> Unit,
    onScrollLongFolderDescriptionChange: (Boolean) -> Unit,
) {
    var showShapeDialog by rememberSaveable { mutableStateOf(false) }
    var showColorDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = stringResource(R.string.add_folder))
                        FolderSettingIcon(selectedFolderIconStyle = selectedFolderIconStyle)
                    }
                },
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
                onClick = { showShapeDialog = true },
                trailingContent = {
                    FolderSettingValue(
                        text = selectedFolderIconStyle.shape.label(),
                    )
                },
            )
            BookMarkerDivider()
            SettingsRow(
                title = stringResource(R.string.folder_color_label),
                onClick = { showColorDialog = true },
                trailingContent = {
                    FolderSettingValue(
                        text = selectedFolderIconStyle.color.label(),
                    )
                },
            )
            BookMarkerDivider()
            SettingsRow(
                title = stringResource(R.string.folder_show_description_label),
                onClick = { onShowFolderDescriptionChange(!showFolderDescription) },
                trailingContent = {
                    Switch(
                        checked = showFolderDescription,
                        onCheckedChange = onShowFolderDescriptionChange,
                    )
                },
            )
            BookMarkerDivider()
            SettingsRow(
                title = stringResource(R.string.folder_scroll_long_description_label),
                onClick = { onScrollLongFolderDescriptionChange(!scrollLongFolderDescription) },
                trailingContent = {
                    Switch(
                        checked = scrollLongFolderDescription,
                        onCheckedChange = onScrollLongFolderDescriptionChange,
                    )
                },
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

@Composable
private fun FolderSettingValue(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun FolderSettingIcon(selectedFolderIconStyle: BookmarkFolderIconStyle) {
    Icon(
        imageVector = selectedFolderIconStyle.shape.iconVector(),
        contentDescription = null,
        tint = selectedFolderIconStyle.color.resolveTint(),
        modifier = Modifier.size(20.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun FolderAppearanceScreenPreview() {
    FolderAppearanceScreen(
        selectedFolderIconStyle = BookmarkFolderIconStyle(
            shape = BookmarkFolderIconShape.FILLED,
            color = BookmarkFolderIconColor.DEFAULT,
        ),
        showFolderDescription = true,
        scrollLongFolderDescription = true,
        onBackClick = {},
        onShapeSelect = {},
        onColorSelect = {},
        onShowFolderDescriptionChange = {},
        onScrollLongFolderDescriptionChange = {},
    )
}
