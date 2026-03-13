package com.hdw.bookmarker.feature.importguide.ui.root

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportBookmarksRootScreen(
    onImportHtmlFileClick: () -> Unit,
    onOpenBrowserGuidesClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.import_bookmarks)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.bookmark_icon_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ImportSourceCard(
                title = stringResource(R.string.import_source_html_file_title),
                description = stringResource(R.string.import_source_html_file_description),
                badges = listOf(
                    stringResource(R.string.import_source_badge_recommended),
                    stringResource(R.string.import_source_badge_direct_import),
                ),
                onClick = onImportHtmlFileClick,
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                },
            )
            ImportSourceCard(
                title = stringResource(R.string.browser_guides_title),
                description = stringResource(R.string.import_source_browser_guides_description),
                badges = emptyList(),
                onClick = onOpenBrowserGuidesClick,
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Public,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                },
            )
        }
    }
}

@Composable
private fun ImportSourceCard(
    title: String,
    description: String,
    badges: List<String>,
    onClick: () -> Unit,
    leadingContent: @Composable () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leadingContent()
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (badges.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    badges.forEach { badge ->
                        SourceBadge(text = badge)
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceBadge(text: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImportBookmarksRootScreenPreview() {
    ImportBookmarksRootScreen(
        onImportHtmlFileClick = {},
        onOpenBrowserGuidesClick = {},
        onBackClick = {},
    )
}
