package com.hdw.bookmarker.feature.home.importer

import androidx.annotation.StringRes

sealed interface BookmarkImportCoordinatorResult {
    data class Success(val snapshotId: String) : BookmarkImportCoordinatorResult

    data class Failure(@param:StringRes val messageResId: Int, val detail: String? = null) :
        BookmarkImportCoordinatorResult
}
