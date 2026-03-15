package com.hdw.bookmarker.development.presentation.tab.deeplink

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.R
import com.hdw.bookmarker.development.presentation.component.DevelopmentActionRow
import com.hdw.bookmarker.development.presentation.tab.TabTitle

@Composable
internal fun DeepLinkScreen(
    onDeepLinkHomeClick: () -> Unit,
    onDeepLinkSettingsClick: () -> Unit,
    onDeepLinkImportGuideClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TabTitle(title = stringResource(id = R.string.development_deep_link_tab_home))
        DevelopmentActionRow(
            title = stringResource(id = R.string.development_deep_link_home),
            onClick = onDeepLinkHomeClick,
        )

        TabTitle(title = stringResource(id = R.string.development_deep_link_tab_settings))
        DevelopmentActionRow(
            title = stringResource(id = R.string.development_deep_link_settings),
            onClick = onDeepLinkSettingsClick,
        )

        TabTitle(title = stringResource(id = R.string.development_deep_link_tab_import_guide))
        DevelopmentActionRow(
            title = stringResource(id = R.string.development_deep_link_import_guide),
            onClick = onDeepLinkImportGuideClick,
        )
    }
}
