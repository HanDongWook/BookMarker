package com.hdw.bookmarker.core.data.repository

import com.hdw.bookmarker.core.model.browser.BrowserInfo

interface BrowserRepository {
    /**
     * 디바이스에 설치된 브라우저 목록을 가져옵니다.
     * @return 설치된 브라우저 정보 리스트
     */
    fun getInstalledBrowsers(): List<BrowserInfo>
}
