package com.hdw.bookmarker.core.domain.util

import kotlin.math.absoluteValue

object BookmarkColorGenerator {
    private val colorValues = listOf(
        0xFFE57373L,
        0xFFF06292L,
        0xFFBA68C8L,
        0xFF9575CDL,
        0xFF7986CBL,
        0xFF64B5F6L,
        0xFF4FC3F7L,
        0xFF4DD0E1L,
        0xFF4DB6ACL,
        0xFF81C784L,
        0xFFAED581L,
        0xFFFFD54FL,
        0xFFFFB74DL,
        0xFFFF8A65L,
        0xFFA1887FL,
        0xFF90A4AEL,
    )

    /** 선택 가능한 북마크 색상 목록 (편집 시 색상 변경용) */
    fun getAllColors(): List<Long> = colorValues

    /** 북마크 고유 ID 기준 색상 생성 */
    fun generateColorForId(id: String): Long {
        val index = id.hashCode().absoluteValue % colorValues.size
        return colorValues[index]
    }
}
