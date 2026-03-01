package com.hdw.bookmarker.feature.home.guide

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.feature.home.R
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun BrowserPickerRoute(
    onBackClick: () -> Unit,
    onOpenDesktopGuide: (Browser, String?) -> Boolean,
) {
    val viewModel: BrowserPickerViewModel = hiltViewModel()
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val resources = LocalResources.current
    var selectedBrowserPackageForImport by rememberSaveable { mutableStateOf<String?>(null) }

    val currentSelectedBrowser = selectedBrowserPackageForImport
        ?.let { pkg -> state.installedBrowsers.find { it.packageName == pkg } }
        ?: state.installedBrowsers.firstOrNull()

    if (selectedBrowserPackageForImport == null) {
        BrowserPickerScreen(
            installedBrowsers = state.installedBrowsers,
            onOpenDesktopGuide = { packageName ->
                selectedBrowserPackageForImport = packageName
            },
            onBackClick = onBackClick,
        )
    } else {
        BackHandler {
            selectedBrowserPackageForImport = null
        }

        BookmarkImportGuideScreen(
            icon = currentSelectedBrowser?.icon,
            browserPackageName = currentSelectedBrowser?.packageName,
            browserName = currentSelectedBrowser?.appName,
            onDismiss = {
                selectedBrowserPackageForImport = null
            },
            onOpenDesktopGuide = {
                val selectedBrowserType = Browser.fromPackageAndName(
                    packageName = currentSelectedBrowser?.packageName,
                    appName = currentSelectedBrowser?.appName,
                )
                if (!onOpenDesktopGuide(selectedBrowserType, currentSelectedBrowser?.packageName)) {
                    context.showShortToast(resources.getString(R.string.import_guide_open_guide_failed))
                }
            },
        )
    }
}
