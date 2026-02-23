package com.hdw.bookmarker.core.model.browser

import java.util.Locale

enum class Browser {
    CHROME,
    BRAVE,
    SAFARI,
    FIREFOX,
    EDGE,
    NAVER_WHALE,
    SAMSUNG_INTERNET,
    OPERA,
    VIVALDI,
    DUCKDUCKGO,
    KIWI,
    YANDEX,
    ARC,
    IE,
    UNKNOWN,

    ;

    companion object {
        fun fromPackageAndName(packageName: String?, appName: String?): Browser {
            val normalizedPackage = packageName.orEmpty().lowercase(Locale.ROOT)
            val normalizedName = appName.orEmpty().lowercase(Locale.ROOT)
            return when {
                normalizedPackage.contains("brave") || normalizedName.contains("brave") -> BRAVE

                normalizedPackage.contains("emmx") || normalizedName.contains("edge") -> EDGE

                normalizedPackage.contains("whale") || normalizedPackage.contains("naver") ||
                    normalizedName.contains("whale") || normalizedName.contains("웨일") ||
                    normalizedName.contains("naver") -> NAVER_WHALE

                normalizedPackage.contains("sbrowser") ||
                    normalizedName.contains("samsung internet") ||
                    normalizedName.contains("삼성 인터넷") -> SAMSUNG_INTERNET

                normalizedPackage.contains("firefox") ||
                    normalizedPackage.contains("fenix") ||
                    normalizedName.contains("firefox") -> FIREFOX

                normalizedPackage.contains("safari") || normalizedName.contains("safari") -> SAFARI

                normalizedPackage.contains("opera") || normalizedName.contains("opera") -> OPERA

                normalizedPackage.contains("vivaldi") || normalizedName.contains("vivaldi") -> VIVALDI

                normalizedPackage.contains("duckduckgo") ||
                    normalizedPackage.contains("ddg") ||
                    normalizedName.contains("duckduckgo") -> DUCKDUCKGO

                normalizedPackage.contains("kiwi") || normalizedName.contains("kiwi") -> KIWI

                normalizedPackage.contains("yandex") || normalizedName.contains("yandex") -> YANDEX

                normalizedPackage.contains("arc") || normalizedName.contains("arc search") -> ARC

                normalizedPackage.contains("chrome") || normalizedName.contains("chrome") -> CHROME

                normalizedPackage.contains("msie") || normalizedName.contains("internet explorer") -> IE

                else -> UNKNOWN
            }
        }
    }
}
