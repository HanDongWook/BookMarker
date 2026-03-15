package com.hdw.bookmarker.development.presentation

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.R
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerTheme
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.feature.settings.presentation.component.SettingsRow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DevelopmentScreen(
    onBackClick: () -> Unit,
    onOpenShowkaseClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.development_activity_title)) },
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
                title = stringResource(id = R.string.development_open_showkase),
                onClick = onOpenShowkaseClick,
            )
            BookMarkerDivider()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DevelopmentScreenPreview() {
    BookMarkerTheme {
        DevelopmentScreen(
            onBackClick = {},
            onOpenShowkaseClick = {},
        )
    }
}