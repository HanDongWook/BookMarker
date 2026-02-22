package com.hdw.bookmarker.feature.settingsetting

import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.hdw.bookmarker.core.ui.BookMarkerDivider
import com.hdw.bookmarker.core.ui.R
import com.hdw.bookmarker.core.ui.util.getAppVersionName
import com.hdw.bookmarker.core.ui.util.getDefaultBrowserPackageFlow
import com.hdw.bookmarker.core.ui.util.getInstalledBrowsers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

@Composable
fun SettingsRoute(onBackClick: () -> Unit, onDefaultBrowserClick: () -> Unit) {
    val viewModel: SettingsViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(context) {
        val installedBrowsers = context.getInstalledBrowsers()
        val persistedSelectedBrowserPackage = context.getDefaultBrowserPackageFlow().first()
        viewModel.initialize(
            appVersion = context.getAppVersionName(),
            installedBrowsers = installedBrowsers,
            persistedSelectedBrowserPackage = persistedSelectedBrowserPackage,
        )
        context.getDefaultBrowserPackageFlow().collectLatest { packageName ->
            viewModel.syncPersistedSelectedBrowser(packageName)
        }
    }

    SettingsScreen(
        onBackClick = onBackClick,
        appVersion = state.appVersion,
        selectedBrowserPackage = state.selectedBrowserPackage,
        selectedBrowserName = state.installedBrowsers
            .firstOrNull { it.packageName == state.selectedBrowserPackage }
            ?.appName
            ?: stringResource(R.string.not_selected),
        onDefaultBrowserClick = onDefaultBrowserClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    appVersion: String,
    selectedBrowserPackage: String?,
    selectedBrowserName: String,
    onDefaultBrowserClick: () -> Unit,
) {
    val resources = LocalResources.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = resources.getString(R.string.settings)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            DefaultBrowserRow(
                title = stringResource(R.string.default_browser_label),
                browserPackageName = selectedBrowserPackage,
                browserName = selectedBrowserName,
                onClick = onDefaultBrowserClick,
            )
            BookMarkerDivider()
            SettingsRow(
                title = stringResource(R.string.app_version_label),
                value = appVersion,
            )
            BookMarkerDivider()
        }
    }
}

@Composable
private fun DefaultBrowserRow(title: String, browserPackageName: String?, browserName: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (browserPackageName != null) {
                BrowserIcon(packageName = browserPackageName)
            }
            Text(
                text = browserName,
                style = MaterialTheme.typography.bodyMedium,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun SettingsRow(title: String, value: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (onClick != null) {
                    it.clickable(onClick = onClick)
                } else {
                    it
                }
            }
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun BrowserIcon(packageName: String) {
    val context = LocalContext.current
    val icon = remember(packageName) {
        runCatching { context.packageManager.getApplicationIcon(packageName) }.getOrNull()
    }
    AndroidView(
        modifier = Modifier.size(20.dp),
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
