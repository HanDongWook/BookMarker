package com.hdw.bookmarker.core.data.repository

import com.hdw.bookmarker.core.model.browser.BrowserInfo
import kotlinx.coroutines.flow.Flow

interface BrowserRepository {
    /**
     * 디바이스에 설치된 브라우저 목록을 가져옵니다.
     * @return 설치된 브라우저 정보 리스트
     */
    fun getInstalledBrowsers(): List<BrowserInfo>

    /**
     * 저장된 북마크 색상 정보를 가져옵니다.
     * @return 브라우저 패키지명과 색상 값(Long)의 맵
     */
    fun getBookmarkColors(): Flow<Map<String, Long>>
}
