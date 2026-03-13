package com.hdw.bookmarker.feature.importguide.route

import kotlinx.serialization.Serializable

@Serializable
sealed interface FeatureBookmarkImportGuideRoute {
    @Serializable
    data object Root : FeatureBookmarkImportGuideRoute

    @Serializable
    data object BrowserGuides : FeatureBookmarkImportGuideRoute

    @Serializable
    data class BrowserGuideDetail(val browserId: String) : FeatureBookmarkImportGuideRoute
}
