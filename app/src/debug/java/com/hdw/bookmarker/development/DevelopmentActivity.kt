package com.hdw.bookmarker.development

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.airbnb.android.showkase.ui.ShowkaseBrowserActivity
import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import com.hdw.bookmarker.core.designsystem.theme.BookMarkerTheme
import com.hdw.bookmarker.core.ui.util.showShortToast
import com.hdw.bookmarker.development.di.BookMarkerShowkaseRootModule
import com.hdw.bookmarker.development.mockdata.DevelopmentMockDataFactory
import com.hdw.bookmarker.development.navigation.DevelopmentDeepLinkNavigator
import com.hdw.bookmarker.development.presentation.DevelopmentNavHost
import com.hdw.bookmarker.main.AppNavigationDeepLink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DevelopmentActivity : ComponentActivity() {
    @Inject
    lateinit var bookmarkRepository: BookmarkRepository

    private val developmentViewModel: DevelopmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookMarkerTheme {
                DevelopmentNavHost(
                    onBackClick = ::finish,
                    onOpenShowkaseClick = {
                        startActivity(
                            ShowkaseBrowserActivity.getIntent(
                                this@DevelopmentActivity,
                                BookMarkerShowkaseRootModule::class.java.canonicalName.orEmpty(),
                            ),
                        )
                    },
                    onInjectMockDataClick = ::injectMockData,
                    onRunPerformanceBenchmarkClick = developmentViewModel::runPerformanceBenchmark,
                    onDeepLinkHomeClick = {
                        DevelopmentDeepLinkNavigator.openMain(
                            this@DevelopmentActivity,
                            AppNavigationDeepLink.Target.Home,
                        )
                    },
                    onDeepLinkSettingsClick = {
                        DevelopmentDeepLinkNavigator.openMain(
                            this@DevelopmentActivity,
                            AppNavigationDeepLink.Target.Settings,
                        )
                    },
                    onDeepLinkImportGuideClick = {
                        DevelopmentDeepLinkNavigator.openMain(
                            this@DevelopmentActivity,
                            AppNavigationDeepLink.Target.ImportGuide,
                        )
                    },
                    performanceSummary = developmentViewModel.performanceSummary,
                    onPerformanceSummaryDismiss = developmentViewModel::dismissPerformanceSummary,
                )
            }
        }
    }

    private fun injectMockData() {
        lifecycleScope.launch {
            val document = DevelopmentMockDataFactory.buildMockDocument()
            bookmarkRepository.saveBookmarkSnapshot(
                snapshotId = null,
                document = document,
                sourceHash = "debug-mock",
                bookmarkColor = 0xFFE53935,
            )
            showShortToast("Mock data snapshot injected")
        }
    }
}
