package com.hdw.bookmarker.feature.importguide

import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.feature.importguide.model.BrowserGuideCatalog
import com.hdw.bookmarker.feature.importguide.model.BrowserGuideItem
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class BrowserPickerState(
    val guideItems: List<BrowserGuideItem> = emptyList(),
)

@HiltViewModel
class BookmarkImportGuideViewModel @Inject constructor(
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
) : ViewModel(),
    ContainerHost<BrowserPickerState, Nothing> {
    override val container = container<BrowserPickerState, Nothing>(BrowserPickerState()) {
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
