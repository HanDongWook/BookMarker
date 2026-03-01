package com.hdw.bookmarker.feature.importguide.picker

import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.core.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.core.model.browser.BrowserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class BrowserPickerState(
    val installedBrowsers: List<BrowserInfo> = emptyList(),
    val selectedBrowserPackageForImport: String? = null,
) {
    val currentSelectedBrowser: BrowserInfo?
        get() = selectedBrowserPackageForImport
            ?.let { pkg -> installedBrowsers.find { it.packageName == pkg } }
            ?: installedBrowsers.firstOrNull()
}

@HiltViewModel
class BrowserPickerViewModel @Inject constructor(
    private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase,
) : ViewModel(), ContainerHost<BrowserPickerState, Nothing> {
    override val container = container<BrowserPickerState, Nothing>(BrowserPickerState()) {
        loadInstalledBrowsers()
    }

    private fun loadInstalledBrowsers() = intent {
        val browsers = getInstalledBrowsersUseCase()
        reduce {
            state.copy(installedBrowsers = browsers)
        }
    }

    fun onBrowserSelected(packageName: String) = intent {
        reduce {
            state.copy(selectedBrowserPackageForImport = packageName)
        }
    }

    fun clearSelectedBrowser() = intent {
        if (state.selectedBrowserPackageForImport == null) return@intent
        reduce {
            state.copy(selectedBrowserPackageForImport = null)
        }
    }
}
