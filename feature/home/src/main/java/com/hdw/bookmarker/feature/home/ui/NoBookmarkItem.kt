package com.hdw.bookmarker.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.ui.R

@Composable
internal fun NoBookmarkItem(modifier: Modifier = Modifier, onImportClick: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.no_browsers_connected),
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onImportClick,
            modifier = Modifier.padding(top = 12.dp),
        ) {
            Text(text = stringResource(R.string.import_bookmarks))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoBookmarkItemPreview() {
    NoBookmarkItem(
        modifier = Modifier.fillMaxSize(),
        onImportClick = {},
    )
}
