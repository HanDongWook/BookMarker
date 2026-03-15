package com.hdw.bookmarker.feature.home.presentation.component.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.ui.R

@Composable
internal fun BookmarkPreviewTopBar(
    title: String?,
    currentUrl: String?,
    onOpenExternally: (String) -> Unit,
    onRefresh: () -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title ?: "",
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = onRefresh,
            enabled = !currentUrl.isNullOrBlank(),
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.bookmark_preview_refresh),
            )
        }
        IconButton(
            onClick = { currentUrl?.let(onOpenExternally) },
            enabled = !currentUrl.isNullOrBlank(),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = stringResource(R.string.bookmark_preview_open_externally),
            )
        }
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.bookmark_preview_close),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarkPreviewTopBarPreview() {
    BookmarkPreviewTopBar(
        title = "OpenAI",
        currentUrl = "https://openai.com",
        onOpenExternally = {},
        onRefresh = {},
        onClose = {},
    )
}
