package com.hdw.bookmarker.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.hdw.bookmarker.core.common.uri.AppUri

@Composable
fun BookmarkSiteImage(iconUri: String?, url: String, title: String, modifier: Modifier = Modifier) {
    val candidateUrl = remember(iconUri, url) {
        iconUri
            ?.takeIf {
                it.startsWith(AppUri.HTTP_SCHEME_PREFIX) ||
                    it.startsWith(AppUri.HTTPS_SCHEME_PREFIX)
            }
            ?: buildFaviconUrl(url)
    }

    AsyncImage(
        model = candidateUrl,
        contentDescription = null,
        modifier = modifier,
        error = rememberVectorPainter(image = Icons.Default.Language),
        fallback = rememberVectorPainter(image = Icons.Default.Language),
        placeholder = rememberVectorPainter(image = Icons.Default.Language),
    )
}

private fun buildFaviconUrl(url: String): String? {
    val host = runCatching { url.toUri().host }.getOrNull() ?: return null
    if (host.isBlank()) return null
    return AppUri.GOOGLE_FAVICON_URL_FORMAT.format(host)
}
