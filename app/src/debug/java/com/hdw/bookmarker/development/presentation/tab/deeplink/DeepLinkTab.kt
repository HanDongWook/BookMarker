package com.hdw.bookmarker.development.presentation.tab.deeplink

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.R
import com.hdw.bookmarker.development.presentation.component.DevelopmentActionRow
import com.hdw.bookmarker.development.presentation.tab.TabTitle

@Composable
internal fun DeepLinkSection(onOpenDeepLinkScreenClick: () -> Unit) {
    TabTitle(
        title = stringResource(id = R.string.development_tab_deep_link),
    )
    DevelopmentActionRow(
        title = stringResource(id = R.string.development_open_deep_link_screen),
        onClick = onOpenDeepLinkScreenClick,
    )
}
