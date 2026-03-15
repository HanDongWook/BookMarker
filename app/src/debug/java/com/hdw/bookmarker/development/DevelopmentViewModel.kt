package com.hdw.bookmarker.development

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hdw.bookmarker.core.data.repository.BookmarkRepository
import com.hdw.bookmarker.development.performance.DevelopmentPerformanceBenchmark
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevelopmentViewModel @Inject constructor(private val bookmarkRepository: BookmarkRepository) : ViewModel() {
    var performanceSummary: String? by mutableStateOf(null)
        private set

    fun runPerformanceBenchmark() {
        viewModelScope.launch {
            performanceSummary = DevelopmentPerformanceBenchmark.run(bookmarkRepository)
        }
    }

    fun dismissPerformanceSummary() {
        performanceSummary = null
    }
}
