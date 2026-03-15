package com.hdw.bookmarker.feature.settings.ui.tab.bookmark

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.model.settings.BookmarkSecondaryDisplayType
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.settings.ui.component.SettingsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkAppearanceScreen(
    scrollLongSecondaryInfo: Boolean,
    secondaryDisplayType: BookmarkSecondaryDisplayType,
    onScrollLongSecondaryInfoChange: (Boolean) -> Unit,
    onSecondaryDisplayTypeChange: (BookmarkSecondaryDisplayType) -> Unit,
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
                title = stringResource(R.string.bookmark_secondary_info_label),
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BookmarkSecondaryDisplayType.values().forEach { type ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { onSecondaryDisplayTypeChange(type) }
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = secondaryDisplayType == type,
                                    onClick = null,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (type) {
                                        BookmarkSecondaryDisplayType.NONE -> stringResource(
                                            R.string.bookmark_secondary_info_none,
                                        )

                                        BookmarkSecondaryDisplayType.URL -> stringResource(
                                            R.string.bookmark_secondary_info_url,
                                        )

                                        BookmarkSecondaryDisplayType.TAG -> stringResource(
                                            R.string.bookmark_secondary_info_tag,
                                        )
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                },
            )
            BookMarkerDivider()
            SettingsRow(
                title = stringResource(R.string.bookmark_scroll_long_info_label),
                onClick = { onScrollLongSecondaryInfoChange(!scrollLongSecondaryInfo) },
                trailingContent = {
                    Switch(
                        checked = scrollLongSecondaryInfo,
                        onCheckedChange = onScrollLongSecondaryInfoChange,
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
        scrollLongSecondaryInfo = true,
        secondaryDisplayType = BookmarkSecondaryDisplayType.URL,
        onScrollLongSecondaryInfoChange = {},
        onSecondaryDisplayTypeChange = {},
        onBackClick = {},
    )
}
