package com.hdw.bookmarker.feature.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hdw.bookmarker.core.model.MimeTypes
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.home.contract.HomeSideEffect
import com.hdw.bookmarker.feature.home.ui.HomeScreen
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeRoute(
    onSettingsClick: () -> Unit,
    onOpenBookmark: (String, String?) -> Boolean,
    onOpenBookmarkImportGuide: () -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val resources = LocalResources.current
    val htmlPickerLauncher = rememberLauncherForActivityResult(OpenDocument()) { uri ->
        if (uri != null) {
            viewModel.onHtmlFileSelected(uri)
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is HomeSideEffect.ShowError -> {
                val message = resources.getString(sideEffect.messageResId)
                val toastText = if (sideEffect.detail.isNullOrBlank()) {
                    message
                } else {
                    resources.getString(R.string.error_with_detail, message, sideEffect.detail)
                }
                context.showShortToast(toastText)
            }

            is HomeSideEffect.ShowMessage -> {
                context.showShortToast(resources.getString(sideEffect.messageResId))
            }

            is HomeSideEffect.OpenFilePicker -> {
                htmlPickerLauncher.launch(arrayOf(MimeTypes.HTML))
            }
        }
    }

    HomeScreen(
        state = state,
        onSettingsClick = onSettingsClick,
        onOpenBookmark = onOpenBookmark,
        onOpenBookmarkImportGuide = onOpenBookmarkImportGuide,
        onSnapshotSelected = viewModel::onSnapshotSelected,
        onSelectedFolderPathChange = viewModel::onSelectedFolderPathChange,
        onBookmarkColorSelected = viewModel::onBookmarkColorSelected,
        onDefaultBrowserSelected = viewModel::onDefaultBrowserSelected,
        onOpenFilePicker = viewModel::openFilePicker,
        onDeleteBookmarkSnapshot = viewModel::deleteBookmarkSnapshot,
        onBookmarkDisplayTypeToggle = viewModel::onBookmarkDisplayTypeToggle,
        onAddBookmarkItem = viewModel::addBookmarkItem,
        onRenameBookmarkSnapshot = viewModel::renameBookmarkSnapshot,
        onDeleteBookmarkItem = viewModel::deleteBookmarkItem,
        onUpdateBookmarkItem = viewModel::updateBookmarkItem,
        onAddEmptyBookmarkSnapshot = viewModel::addEmptyBookmarkSnapshot,
    )
}
