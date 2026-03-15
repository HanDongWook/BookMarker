package com.hdw.bookmarker.feature.home.presentation.component.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hdw.bookmarker.core.model.bookmark.SnapshotId
import com.hdw.bookmarker.core.model.folderstyle.BookmarkFolderIconStyle
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.component.BookmarkerTextField
import com.hdw.bookmarker.feature.home.domain.model.BookmarkSnapshots
import com.hdw.bookmarker.feature.home.domain.search.BookmarkSearchEngine
import com.hdw.bookmarker.feature.home.domain.search.model.BookmarkSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private sealed interface BookmarkSearchUiState {
    data object Empty : BookmarkSearchUiState

    data object Loading : BookmarkSearchUiState

    data object NoResults : BookmarkSearchUiState

    data class Results(val results: List<BookmarkSearchResult>) : BookmarkSearchUiState
}

@Composable
internal fun BookmarkSearchDialog(
    query: String,
    library: BookmarkSnapshots,
    snapshotTitles: Map<SnapshotId, String>,
    folderIconStyle: BookmarkFolderIconStyle,
    onQueryChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onResultClick: (BookmarkSearchResult) -> Unit,
) {
    val searchEngine = remember { BookmarkSearchEngine() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val searchUiState by produceState<BookmarkSearchUiState>(
        initialValue = BookmarkSearchUiState.Empty,
        query,
        library,
        snapshotTitles,
    ) {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) {
            value = BookmarkSearchUiState.Empty
            return@produceState
        }

        value = BookmarkSearchUiState.Loading
        val results = withContext(Dispatchers.Default) {
            searchEngine.search(
                query = normalizedQuery,
                library = library,
                snapshotTitles = snapshotTitles,
            )
        }
        value = if (results.isEmpty()) {
            BookmarkSearchUiState.NoResults
        } else {
            BookmarkSearchUiState.Results(results)
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismissRequest,
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.82f)
                    .widthIn(max = 760.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = Color(android.graphics.Color.WHITE),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    BookmarkerTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .focusRequester(focusRequester),
                        singleLine = true,
                        leadingIcon = {
                            IconButton(onClick = onDismissRequest) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.bookmark_search_close),
                                )
                            }
                        },
                        trailingIcon = {
                            if (query.isNotBlank()) {
                                IconButton(onClick = { onQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = stringResource(R.string.bookmark_search_clear),
                                    )
                                }
                            }
                        },
                        placeholder = {
                            Text(text = stringResource(R.string.bookmark_search_hint))
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                            },
                        ),
                    )

                    when (val currentState = searchUiState) {
                        BookmarkSearchUiState.Empty -> {
                            BookmarkSearchMessage(
                                text = stringResource(R.string.bookmark_search_empty),
                            )
                        }

                        BookmarkSearchUiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    CircularProgressIndicator()
                                    Text(text = stringResource(R.string.bookmark_search_loading))
                                }
                            }
                        }

                        BookmarkSearchUiState.NoResults -> {
                            BookmarkSearchMessage(
                                text = stringResource(R.string.bookmark_search_no_results),
                            )
                        }

                        is BookmarkSearchUiState.Results -> {
                            BookmarkSearchResultList(
                                results = currentState.results,
                                folderIconStyle = folderIconStyle,
                                onResultClick = {
                                    keyboardController?.hide()
                                    onResultClick(it)
                                },
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkSearchMessage(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
