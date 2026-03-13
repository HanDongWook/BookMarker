package com.hdw.bookmarker.feature.settings.ui.tab.appearance

import androidx.compose.foundation.isSystemInDarkTheme
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
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.label
import com.hdw.bookmarker.feature.settings.ui.component.SettingsRow
import com.hdw.bookmarker.feature.settings.ui.tab.theme.ThemeModeDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    selectedThemeMode: String?,
    folderIconStyle: BookmarkFolderIconStyle,
    onBackClick: () -> Unit,
    onThemeModeSelect: (String) -> Unit,
    onFolderClick: () -> Unit,
    onBookmarkClick: () -> Unit,
) {
    val effectiveThemeMode = selectedThemeMode ?: if (isSystemInDarkTheme()) {
        SettingsRepository.APP_THEME_MODE_DARK
    } else {
        SettingsRepository.APP_THEME_MODE_LIGHT
    }
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.appearance_label)) },
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
                title = stringResource(R.string.theme_label),
                value = if (effectiveThemeMode == SettingsRepository.APP_THEME_MODE_DARK) {
                    stringResource(R.string.theme_dark)
                } else {
                    stringResource(R.string.theme_light)
                },
                onClick = { showThemeDialog = true },
            )
            BookMarkerDivider()

            SettingsRow(
                title = stringResource(R.string.add_folder),
                value = "${folderIconStyle.shape.label()} / ${folderIconStyle.color.label()}",
                onClick = onFolderClick,
            )
            BookMarkerDivider()

            SettingsRow(
                title = stringResource(R.string.add_bookmark),
                onClick = onBookmarkClick,
            )
            BookMarkerDivider()
        }
    }

    if (showThemeDialog) {
        ThemeModeDialog(
            selectedThemeMode = effectiveThemeMode,
            onDismiss = { showThemeDialog = false },
            onThemeModeSelect = { themeMode ->
                onThemeModeSelect(themeMode)
                showThemeDialog = false
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppearanceScreenPreview() {
    AppearanceScreen(
        selectedThemeMode = SettingsRepository.APP_THEME_MODE_LIGHT,
        folderIconStyle = BookmarkFolderIconStyle(
            shape = BookmarkFolderIconShape.FILLED,
            color = BookmarkFolderIconColor.DEFAULT,
        ),
        onBackClick = {},
        onThemeModeSelect = {},
        onFolderClick = {},
        onBookmarkClick = {},
    )
}
