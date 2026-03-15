package com.hdw.bookmarker.development.presentation.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DevelopmentTopAppBar(
    isDeepLinkScreen: Boolean,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(
                    id = if (isDeepLinkScreen) {
                        R.string.development_tab_deep_link
                    } else {
                        R.string.development_activity_title
                    },
                ),
            )
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
}
