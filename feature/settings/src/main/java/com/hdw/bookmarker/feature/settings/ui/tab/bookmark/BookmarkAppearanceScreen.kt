package com.hdw.bookmarker.feature.settings.ui.tab.bookmark

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.ui.component.SettingsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkAppearanceScreen(
    showBookmarkUrl: Boolean,
    onShowBookmarkUrlChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.add_bookmark)) },
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
                title = stringResource(R.string.bookmark_show_url_label),
                onClick = { onShowBookmarkUrlChange(!showBookmarkUrl) },
                trailingContent = {
                    Switch(
                        checked = showBookmarkUrl,
                        onCheckedChange = onShowBookmarkUrlChange,
                    )
                },
            )
            BookMarkerDivider()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkAppearanceScreenPreview() {
    BookmarkAppearanceScreen(
        showBookmarkUrl = true,
        onShowBookmarkUrlChange = {},
        onBackClick = {},
    )
}
