package com.hdw.bookmarker.main

import android.content.Intent
import androidx.core.util.PatternsCompat
import com.hdw.bookmarker.feature.home.domain.model.QuickSaveBookmarkSeed

internal class QuickSaveIntentParser {
    fun parse(intent: Intent?): QuickSaveBookmarkSeed? {
        if (intent?.action != Intent.ACTION_SEND) return null
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty().trim()
        val extractedUrl = extractFirstUrl(sharedText) ?: return null
        val rawTitle = intent.getStringExtra(Intent.EXTRA_TITLE)
            ?: intent.getStringExtra(Intent.EXTRA_SUBJECT)
            ?: extractedUrl
        val cleanedDescription = sharedText
            .replace(extractedUrl, "")
            .trim()
            .takeIf { it.isNotBlank() }
            .orEmpty()
        return QuickSaveBookmarkSeed(
            title = rawTitle.trim().ifBlank { extractedUrl },
            url = extractedUrl,
            description = cleanedDescription,
        )
    }

    private fun extractFirstUrl(text: String): String? {
        if (text.isBlank()) return null
        val matcher = PatternsCompat.WEB_URL.matcher(text)
        while (matcher.find()) {
            val candidate = matcher.group().trim()
            if (candidate.startsWith("http://") || candidate.startsWith("https://")) {
                return candidate
            }
        }
        return null
    }
}
