package com.hdw.bookmarker.feature.importguide

import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.feature.importguide.model.BrowserGuideCatalog
import com.hdw.bookmarker.feature.importguide.model.BrowserGuideItem
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class BookmarkImportGuideState(val guideItems: List<BrowserGuideItem> = emptyList())

@HiltViewModel
class BookmarkImportGuideViewModel @Inject constructor(
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
) : ViewModel(),
    ContainerHost<BookmarkImportGuideState, Nothing> {
    override val container = container<BookmarkImportGuideState, Nothing>(BookmarkImportGuideState()) {
        loadInstalledBrowsers()
    }

    private fun loadInstalledBrowsers() = intent {
        val browsers = getInstalledBrowsersUseCase()
        reduce {
            state.copy(
                guideItems = BrowserGuideCatalog.buildGuideItems(browsers),
            )
        }
    }
}
