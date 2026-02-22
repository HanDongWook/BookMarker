package com.hdw.bookmarker.feature.settingsetting.defaultbrowser

import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.InstalledBrowserInfo
import com.hdw.bookmarker.core.ui.util.getAppVersionName
import com.hdw.bookmarker.core.ui.util.getInstalledBrowsers
import com.hdw.bookmarker.feature.settingsetting.SettingsViewModel

@Composable
fun DefaultBrowserRoute(onBackClick: () -> Unit) {
    val viewModel: SettingsViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(context) {
        viewModel.initialize(
            appVersion = context.getAppVersionName(),
            installedBrowsers = context.getInstalledBrowsers(),
        )
    }

    DefaultBrowserScreen(
        installedBrowsers = state.installedBrowsers,
        selectedBrowserPackage = state.selectedBrowserPackage,
        onBackClick = onBackClick,
        onBrowserSelect = { packageName ->
            viewModel.selectDefaultBrowser(packageName)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultBrowserScreen(
    installedBrowsers: List<InstalledBrowserInfo>,
    selectedBrowserPackage: String?,
    onBackClick: () -> Unit,
    onBrowserSelect: (String) -> Unit,
) {
    val resources = LocalResources.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = resources.getString(R.string.default_browser_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        if (installedBrowsers.isEmpty()) {
            Text(
                text = stringResource(R.string.no_browsers_connected),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                textAlign = TextAlign.Center,
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            items(installedBrowsers, key = { it.packageName }) { browser ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBrowserSelect(browser.packageName) }
                        .padding(horizontal = 20.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BrowserIcon(packageName = browser.packageName)
                    Text(
                        text = browser.appName,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    RadioButton(
                        selected = selectedBrowserPackage == browser.packageName,
                        onClick = { onBrowserSelect(browser.packageName) },
                    )
                }
            }
        }
    }
}

@Composable
private fun BrowserIcon(packageName: String) {
    val context = LocalContext.current
    val icon = remember(packageName) {
        runCatching { context.packageManager.getApplicationIcon(packageName) }.getOrNull()
    }
    AndroidView(
        modifier = Modifier.size(24.dp),
        factory = { viewContext ->
            ImageView(viewContext).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
        },
        update = { imageView ->
            imageView.setImageDrawable(icon)
        },
    )
}
