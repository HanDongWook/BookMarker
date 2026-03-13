package com.hdw.bookmarker.feature.home.ui.share

sealed interface BookmarkExportAction {
    data object ShareText : BookmarkExportAction
    data object ShareHtml : BookmarkExportAction
    data object SaveText : BookmarkExportAction
    data object SaveHtml : BookmarkExportAction
}
