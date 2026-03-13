package com.hdw.bookmarker.feature.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hdw.bookmarker.core.model.MimeTypes
import com.hdw.bookmarker.core.model.bookmark.BookmarkDocument
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.home.contract.HomeSideEffect
import com.hdw.bookmarker.feature.home.ui.HomeScreen
import com.hdw.bookmarker.feature.home.ui.share.BookmarkExportAction
import com.hdw.bookmarker.feature.home.ui.share.buildBookmarkExportFileName
import com.hdw.bookmarker.feature.home.ui.share.buildBookmarkExportHtmlContent
import com.hdw.bookmarker.feature.home.ui.share.buildBookmarkExportTextContent
import com.hdw.bookmarker.feature.home.ui.share.shareCurrentBookmarkHtmlExport
import com.hdw.bookmarker.feature.home.ui.share.shareCurrentBookmarkTextExport
import com.hdw.bookmarker.feature.home.ui.share.saveBookmarkExportContent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

private data class PendingBookmarkFileExport(
    val fileName: String,
    val content: String,
)

@Composable
fun HomeRoute(
    onSettingsClick: () -> Unit,
    onOpenBookmark: (String, String?) -> Boolean,
    onOpenBookmarkImportGuide: () -> Unit,
    pendingImportHtmlRequestToken: Long? = null,
    onImportHtmlRequestHandled: () -> Unit = {},
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val resources = LocalResources.current
    var pendingTextFileExport by remember { mutableStateOf<PendingBookmarkFileExport?>(null) }
    var pendingHtmlFileExport by remember { mutableStateOf<PendingBookmarkFileExport?>(null) }
    val htmlPickerLauncher = rememberLauncherForActivityResult(OpenDocument()) { uri ->
        if (uri != null) {
            viewModel.onHtmlFileSelected(uri)
        }
    }
    val textFileSaverLauncher = rememberLauncherForActivityResult(CreateDocument("text/plain")) { uri ->
        val pendingExport = pendingTextFileExport
        pendingTextFileExport = null
        if (uri == null || pendingExport == null) return@rememberLauncherForActivityResult
        val messageResId = if (saveBookmarkExportContent(context, uri, pendingExport.content)) {
            R.string.export_current_bookmarks_saved
        } else {
            R.string.export_current_bookmarks_save_failed
        }
        context.showShortToast(resources.getString(messageResId))
    }
    val htmlFileSaverLauncher = rememberLauncherForActivityResult(CreateDocument("text/html")) { uri ->
        val pendingExport = pendingHtmlFileExport
        pendingHtmlFileExport = null
        if (uri == null || pendingExport == null) return@rememberLauncherForActivityResult
        val messageResId = if (saveBookmarkExportContent(context, uri, pendingExport.content)) {
            R.string.export_current_bookmarks_saved
        } else {
            R.string.export_current_bookmarks_save_failed
        }
        context.showShortToast(resources.getString(messageResId))
    }

    LaunchedEffect(pendingImportHtmlRequestToken) {
        if (pendingImportHtmlRequestToken != null) {
            onImportHtmlRequestHandled()
            htmlPickerLauncher.launch(arrayOf(MimeTypes.HTML))
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
        }
    }

    val onBookmarkExportRequest: (BookmarkExportAction, BookmarkDocument) -> Unit = { action, document ->
        when (action) {
            BookmarkExportAction.ShareText -> {
                if (!shareCurrentBookmarkTextExport(context, document)) {
                    context.showShortToast(resources.getString(R.string.export_current_bookmarks_empty))
                }
            }

            BookmarkExportAction.ShareHtml -> {
                when {
                    !shareCurrentBookmarkHtmlExport(context, document) -> {
                        context.showShortToast(resources.getString(R.string.export_current_bookmarks_html_failed))
                    }
                }
            }

            BookmarkExportAction.SaveText -> {
                val textContent = buildBookmarkExportTextContent(
                    bookmarkDocument = document,
                    fallbackTitle = resources.getString(R.string.export_current_bookmarks_label),
                )
                if (textContent == null) {
                    context.showShortToast(resources.getString(R.string.export_current_bookmarks_empty))
                } else {
                    val pendingExport = PendingBookmarkFileExport(
                        fileName = buildBookmarkExportFileName(document, extension = "txt"),
                        content = textContent,
                    )
                    pendingTextFileExport = pendingExport
                    textFileSaverLauncher.launch(pendingExport.fileName)
                }
            }

            BookmarkExportAction.SaveHtml -> {
                val htmlContent = buildBookmarkExportHtmlContent(document)
                if (htmlContent == null) {
                    context.showShortToast(resources.getString(R.string.export_current_bookmarks_empty))
                } else {
                    val pendingExport = PendingBookmarkFileExport(
                        fileName = buildBookmarkExportFileName(document, extension = "html"),
                        content = htmlContent,
                    )
                    pendingHtmlFileExport = pendingExport
                    htmlFileSaverLauncher.launch(pendingExport.fileName)
                }
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
        onDeleteBookmarkSnapshot = viewModel::deleteBookmarkSnapshot,
        onBookmarkDisplayTypeToggle = viewModel::onBookmarkDisplayTypeToggle,
        onAddBookmarkItem = viewModel::addBookmarkItem,
        onRenameBookmarkSnapshot = viewModel::renameBookmarkSnapshot,
        onDeleteBookmarkItem = viewModel::deleteBookmarkItem,
        onUpdateBookmarkItem = viewModel::updateBookmarkItem,
        onAddEmptyBookmarkSnapshot = viewModel::addEmptyBookmarkSnapshot,
        onBookmarkExportRequest = onBookmarkExportRequest,
    )
}
