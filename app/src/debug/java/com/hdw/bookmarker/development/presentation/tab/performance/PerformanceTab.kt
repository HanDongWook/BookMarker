package com.hdw.bookmarker.development.presentation.tab.performance

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.R
import com.hdw.bookmarker.development.presentation.component.DevelopmentActionRow
import com.hdw.bookmarker.development.presentation.tab.TabTitle

@Composable
fun PerformanceTab(
    onRunPerformanceBenchmarkClick: () -> Unit,
    performanceSummary: String?,
    onPerformanceSummaryDismiss: () -> Unit,
) {
    TabTitle(
        title = stringResource(id = R.string.development_tab_performance),
    )
    DevelopmentActionRow(
        title = stringResource(id = R.string.development_run_performance_benchmark),
        onClick = onRunPerformanceBenchmarkClick,
    )

    if (!performanceSummary.isNullOrBlank()) {
        AlertDialog(
            onDismissRequest = onPerformanceSummaryDismiss,
            title = { Text(text = stringResource(id = R.string.development_performance_dialog_title)) },
            text = { Text(text = performanceSummary) },
            confirmButton = {
                TextButton(onClick = onPerformanceSummaryDismiss) {
                    Text(text = stringResource(id = R.string.development_close))
                }
            },
        )
    }
}
