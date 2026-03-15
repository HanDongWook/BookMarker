package com.hdw.bookmarker.development.presentation.tab.mockdata

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.R
import com.hdw.bookmarker.development.presentation.component.DevelopmentActionRow
import com.hdw.bookmarker.development.presentation.tab.TabTitle

@Composable
fun MockDataTab(onInjectMockDataClick: () -> Unit) {
    TabTitle(
        title = stringResource(id = R.string.development_tab_mock_data),
    )
    DevelopmentActionRow(
        title = stringResource(id = R.string.development_inject_mock_data),
        onClick = onInjectMockDataClick,
    )
}
