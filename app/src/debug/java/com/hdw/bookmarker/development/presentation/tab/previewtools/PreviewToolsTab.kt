package com.hdw.bookmarker.development.presentation.tab.previewtools

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.R
import com.hdw.bookmarker.development.presentation.component.DevelopmentActionRow
import com.hdw.bookmarker.development.presentation.tab.TabTitle

@Composable
internal fun PreviewToolsTab(onOpenShowkaseClick: () -> Unit) {
    TabTitle(
        title = stringResource(id = R.string.development_tab_preview_tools),
    )
    DevelopmentActionRow(
        title = stringResource(id = R.string.development_open_showkase),
        onClick = onOpenShowkaseClick,
    )
}
