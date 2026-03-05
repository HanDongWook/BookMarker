package com.hdw.bookmarker.feature.importguide.detail

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.ui.R

@Composable
fun BookmarkImportGuideScreen(
    icon: Drawable?,
    browserPackageName: String?,
    browserName: String?,
    onDismiss: () -> Unit,
    onOpenDesktopGuide: () -> Unit,
    iconModifier: Modifier = Modifier,
    browserNameModifier: Modifier = Modifier,
) {
    val browser = remember(browserPackageName, browserName) {
        Browser.fromPackageAndName(
            packageName = browserPackageName,
            appName = browserName,
        )
    }
    val resolvedBrowserName = browserName ?: stringResource(R.string.browser_generic_name)
    val step1Guide = browser.toStep1GuideContent(resolvedBrowserName = resolvedBrowserName)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            GuideTitle(
                icon = icon,
                iconModifier = iconModifier,
                browserName = browserName,
                browserNameModifier = browserNameModifier,
                onDismiss = onDismiss,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (browserName.isNullOrBlank()) {
                    stringResource(R.string.import_guide_title)
                } else {
                    stringResource(R.string.import_guide_title_for_browser, browserName)
                },
                style = MaterialTheme.typography.headlineSmall,
            )

            Spacer(modifier = Modifier.height(16.dp))

            GuideSection(
                title = step1Guide.step1Title,
                descriptions = step1Guide.step1Descriptions,
                buttonText = if (step1Guide.showDesktopGuideButton) {
                    stringResource(R.string.import_guide_step1_button)
                } else {
                    null
                },
                onClick = if (step1Guide.showDesktopGuideButton) onOpenDesktopGuide else null,
            )

            Spacer(modifier = Modifier.height(12.dp))

            GuideSection(
                title = stringResource(R.string.import_guide_step2_title),
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GuideTitle(
    icon: Drawable?,
    iconModifier: Modifier = Modifier,
    browserName: String? = null,
    browserNameModifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        if (icon != null) {
            Image(
                modifier = iconModifier.size(56.dp),
                painter = rememberDrawablePainter(drawable = icon),
                contentDescription = null,
            )
        }
        if (!browserName.isNullOrBlank()) {
            Text(
                text = browserName,
                style = MaterialTheme.typography.titleLarge,
                modifier = browserNameModifier.padding(start = 12.dp, top = 12.dp),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onDismiss) {
            Text(text = stringResource(R.string.import_guide_exit))
        }
    }
}

@Composable
private fun GuideSection(
    title: String,
    vararg descriptions: String,
    buttonText: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            descriptions.forEach { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (buttonText != null && onClick != null) {
                Button(onClick = onClick) {
                    Text(text = buttonText)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GuideTitlePreview() {
    GuideTitle(
        icon = null,
        browserName = "Chrome",
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun GuideSectionPreview() {
    GuideSection(
        "1. 브라우저 북마크 내보내기",
        "브라우저 설정에서 북마크 관리자 메뉴로 이동하세요.",
        "북마크 내보내기를 눌러 HTML 파일을 저장하세요.",
    )
}

@Preview(showBackground = true)
@Composable
private fun BookmarkImportGuideScreenPreview() {
    BookmarkImportGuideScreen(
        icon = null,
        browserPackageName = "com.android.chrome",
        browserName = "Chrome",
        onDismiss = {},
        onOpenDesktopGuide = {},
    )
}
