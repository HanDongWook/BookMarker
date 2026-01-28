package com.hdw.bookmarker.main

import androidx.lifecycle.ViewModel
import com.hdw.bookmarker.domain.usecase.GetInstalledBrowsersUseCase
import com.hdw.bookmarker.model.BrowserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val getInstalledBrowsersUseCase: GetInstalledBrowsersUseCase) :
    ViewModel() {

    fun getInstalledBrowsers(): List<BrowserInfo> = getInstalledBrowsersUseCase()
}
