package com.hdw.bookmarker.feature.importguide.route

import kotlinx.serialization.Serializable

@Serializable
sealed interface FeatureBookmarkImportGuideRoute {
    @Serializable
    data object Picker : FeatureBookmarkImportGuideRoute

    @Serializable
    data class GuideFeatureBookmark(val packageName: String) : FeatureBookmarkImportGuideRoute
}