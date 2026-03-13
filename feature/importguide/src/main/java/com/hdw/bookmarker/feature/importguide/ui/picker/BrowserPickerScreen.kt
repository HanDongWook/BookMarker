package com.hdw.bookmarker.feature.importguide.ui.picker

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.feature.importguide.model.BrowserGuideItem
import com.hdw.bookmarker.feature.importguide.ui.component.BrowserGuideIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserPickerScreen(
    guideItems: List<BrowserGuideItem>,
    onOpenDesktopGuide: (com.hdw.bookmarker.core.model.browser.Browser) -> Unit,
    onBackClick: () -> Unit,
    iconModifierForBrowser: @Composable (BrowserGuideItem) -> Modifier = { Modifier },
    textModifierForBrowser: @Composable (BrowserGuideItem) -> Modifier = { Modifier },
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.browser_guides_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.bookmark_icon_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
        ) {
            items(guideItems, key = { it.browser.name }) { guideItem ->
                BrowserItem(
                    guideItem = guideItem,
                    onSyncClick = { onOpenDesktopGuide(guideItem.browser) },
                    iconModifier = iconModifierForBrowser(guideItem),
                    textModifier = textModifierForBrowser(guideItem),
                )
            }
        }
    }
}

@Composable
private fun BrowserItem(
    guideItem: BrowserGuideItem,
    onSyncClick: () -> Unit,
    iconModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onSyncClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrowserGuideIcon(
            browser = guideItem.browser,
            displayName = guideItem.displayName,
            installedIcon = guideItem.installedBrowser?.icon,
            modifier = iconModifier,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = guideItem.displayName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = textModifier.weight(1f),
        )
        if (guideItem.isInstalled) {
            InstalledBadge()
            Spacer(modifier = Modifier.width(12.dp))
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowRight,
            contentDescription = stringResource(R.string.sync),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BrowserItemPreview() {
    BrowserItem(
        guideItem = BrowserGuideItem(
            browser = com.hdw.bookmarker.core.model.browser.Browser.CHROME,
            displayName = "Chrome",
            installedBrowser = com.hdw.bookmarker.core.model.browser.BrowserInfo(
                packageName = "com.android.chrome",
                appName = "Chrome",
                icon = ColorDrawable(android.graphics.Color.GRAY),
            ),
        ),
        onSyncClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun BrowserPickerScreenPreview() {
    BrowserPickerScreen(
        guideItems = listOf(
            BrowserGuideItem(
                browser = com.hdw.bookmarker.core.model.browser.Browser.CHROME,
                displayName = "Chrome",
                installedBrowser = com.hdw.bookmarker.core.model.browser.BrowserInfo(
                    packageName = "com.android.chrome",
                    appName = "Chrome",
                    icon = ColorDrawable(android.graphics.Color.GRAY),
                ),
            ),
            BrowserGuideItem(
                browser = com.hdw.bookmarker.core.model.browser.Browser.SAFARI,
                displayName = "Safari",
            ),
        ),
        onOpenDesktopGuide = {},
        onBackClick = {},
    )
}

@Composable
private fun InstalledBadge() {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Text(
            text = stringResource(R.string.import_guide_installed_badge),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}
