package com.hdw.bookmarker.feature.settings.presentation.component.tab.temporarydata

import android.content.Context
import com.hdw.bookmarker.core.ui.util.getTemporaryDataSizeDisplay
import com.hdw.bookmarker.feature.settings.presentation.model.DisplayValueState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend fun loadTemporaryDataSize(context: Context): DisplayValueState = withContext(Dispatchers.IO) {
    runCatching {
        context.getTemporaryDataSizeDisplay()
    }.getOrNull()
        ?.let(DisplayValueState::Loaded)
        ?: DisplayValueState.Unavailable
}
