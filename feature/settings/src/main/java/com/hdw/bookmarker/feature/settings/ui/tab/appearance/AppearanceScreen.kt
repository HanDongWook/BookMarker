package com.hdw.bookmarker.feature.settings.ui.tab.appearance

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.hdw.bookmarker.core.data.repository.SettingsRepository
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconColor
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconShape
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.folderstyle.iconVector
import com.hdw.bookmarker.core.ui.folderstyle.label
import com.hdw.bookmarker.core.ui.folderstyle.resolveTint
import com.hdw.bookmarker.feature.settings.ui.component.SettingsRow
import com.hdw.bookmarker.feature.settings.ui.tab.language.AppLanguageDialog
import com.hdw.bookmarker.feature.settings.ui.tab.language.AppLanguageRow
import com.hdw.bookmarker.feature.settings.ui.tab.theme.ThemeModeDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    selectedThemeMode: String?,
    folderIconStyle: BookmarkFolderIconStyle,
    onBackClick: () -> Unit,
    onThemeModeSelect: (String) -> Unit,
    onLargeScreenClick: () -> Unit,
    onFolderClick: () -> Unit,
    onBookmarkClick: () -> Unit,
) {
    val effectiveThemeMode = selectedThemeMode ?: if (isSystemInDarkTheme()) {
        SettingsRepository.APP_THEME_MODE_DARK
    } else {
        SettingsRepository.APP_THEME_MODE_LIGHT
    }
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    var selectedLanguageTag by rememberSaveable {
        mutableStateOf(AppCompatDelegate.getApplicationLocales().toLanguageTags().substringBefore(","))
    }
    var showAppLanguageDialog by rememberSaveable { mutableStateOf(false) }

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

            AppLanguageRow(
                languageTag = selectedLanguageTag,
                onClick = { showAppLanguageDialog = true },
            )
            BookMarkerDivider()

            SettingsRow(
                title = stringResource(R.string.large_screen_label),
                onClick = onLargeScreenClick,
            )
            BookMarkerDivider()

            SettingsRow(
                title = stringResource(R.string.add_folder),
                onClick = onFolderClick,
                trailingContent = {
                    FolderAppearanceValue(
                        folderIconStyle = folderIconStyle,
                        text = "${folderIconStyle.shape.label()} / ${folderIconStyle.color.label()}",
                    )
                },
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

    if (showAppLanguageDialog) {
        AppLanguageDialog(
            selectedLanguageTag = selectedLanguageTag,
            onDismiss = { showAppLanguageDialog = false },
            onLanguageSelect = { languageTag ->
                selectedLanguageTag = languageTag
                val localeList = if (languageTag.isBlank()) {
                    LocaleListCompat.getEmptyLocaleList()
                } else {
                    LocaleListCompat.forLanguageTags(languageTag)
                }
                AppCompatDelegate.setApplicationLocales(localeList)
                showAppLanguageDialog = false
            },
        )
    }
}

@Composable
private fun FolderAppearanceValue(folderIconStyle: BookmarkFolderIconStyle, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = folderIconStyle.shape.iconVector(),
            contentDescription = null,
            tint = folderIconStyle.color.resolveTint(),
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
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
        onLargeScreenClick = {},
        onFolderClick = {},
        onBookmarkClick = {},
    )
}
