package com.hdw.bookmarker.feature.trends.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerTheme
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.runtime.presenter.presenterOf
import com.slack.circuit.runtime.ui.ui
import com.hdw.bookmarker.feature.trends.presentation.component.TrendsScreen

@Composable
fun TrendsRoute() {
    val presenter = remember { presenterOf<TrendsUiState> { TrendsUiState } }
    val ui = remember {
        ui<TrendsUiState> { _, modifier ->
            TrendsScreen(modifier = modifier)
        }
    }

    CircuitContent(
        screen = TrendsCircuitScreen,
        presenter = presenter,
        ui = ui,
    )
}

@Preview(showBackground = true)
@Composable
private fun TrendsRoutePreview() {
    BookMarkerTheme {
        TrendsRoute()
    }
}
