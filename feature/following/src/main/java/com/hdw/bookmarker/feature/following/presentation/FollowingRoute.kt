package com.hdw.bookmarker.feature.following.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerTheme
import com.hdw.bookmarker.feature.following.presentation.component.FollowingScreen
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.runtime.presenter.presenterOf
import com.slack.circuit.runtime.ui.ui

@Composable
fun FollowingRoute() {
    val presenter = remember { presenterOf<FollowingUiState> { FollowingUiState } }
    val ui = remember {
        ui<FollowingUiState> { _, modifier ->
            FollowingScreen(modifier = modifier)
        }
    }

    CircuitContent(
        screen = FollowingCircuitScreen,
        presenter = presenter,
        ui = ui,
    )
}

@Preview(showBackground = true)
@Composable
private fun FollowingRoutePreview() {
    BookMarkerTheme {
        FollowingRoute()
    }
}
