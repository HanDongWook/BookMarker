package com.hdw.bookmarker.domain.repository

import com.hdw.bookmarker.model.BrowserInfo

interface BrowserRepository {
    /**
     * 디바이스에 설치된 브라우저 목록을 가져옵니다.
     * @return 설치된 브라우저 정보 리스트
     */
    fun getInstalledBrowsers(): List<BrowserInfo>
}
