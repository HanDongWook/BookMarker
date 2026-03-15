package com.hdw.bookmarker.development.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.hdw.bookmarker.development.presentation.tab.deeplink.DeepLinkSection
import com.hdw.bookmarker.development.presentation.tab.mockdata.MockDataTab
import com.hdw.bookmarker.development.presentation.tab.performance.PerformanceTab
import com.hdw.bookmarker.development.presentation.tab.previewtools.PreviewToolsTab

@Composable
internal fun DevelopmentScreen(
    onOpenDeepLinkScreenClick: () -> Unit,
    onOpenShowkaseClick: () -> Unit,
    onInjectMockDataClick: () -> Unit,
    onRunPerformanceBenchmarkClick: () -> Unit,
    performanceSummary: String?,
    onPerformanceSummaryDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        MockDataTab(
            onInjectMockDataClick = onInjectMockDataClick,
        )

        PerformanceTab(
            onRunPerformanceBenchmarkClick = onRunPerformanceBenchmarkClick,
            performanceSummary = performanceSummary,
            onPerformanceSummaryDismiss = onPerformanceSummaryDismiss,
        )

        DeepLinkSection(
            onOpenDeepLinkScreenClick = onOpenDeepLinkScreenClick,
        )

        PreviewToolsTab(
            onOpenShowkaseClick = onOpenShowkaseClick,
        )
    }
}
