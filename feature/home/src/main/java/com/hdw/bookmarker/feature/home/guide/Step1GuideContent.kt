package com.hdw.bookmarker.feature.home.guide

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.feature.home.R

data class Step1GuideContent(
    val step1Title: String,
    val step1Descriptions: Array<String>,
    val showDesktopGuideButton: Boolean,
)

@Composable
fun Browser.toStep1GuideContent(resolvedBrowserName: String): Step1GuideContent = when (this) {
    Browser.CHROME -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice),
                stringResource(R.string.import_guide_step1_body_desktop),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.BRAVE -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_brave),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_brave),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.EDGE -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_edge),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_edge),
                stringResource(R.string.import_guide_step1_body_desktop_edge),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.NAVER_WHALE -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_naver_whale),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_naver_whale),
                stringResource(R.string.import_guide_step1_body_desktop_naver_whale),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.SAMSUNG_INTERNET -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_samsung_internet),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_samsung_internet),
                stringResource(R.string.import_guide_step1_body_desktop_samsung_internet),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.FIREFOX -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_firefox),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_firefox),
                stringResource(R.string.import_guide_step1_body_desktop_firefox),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.SAFARI -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_safari),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_safari),
                stringResource(R.string.import_guide_step1_body_desktop_safari),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.OPERA -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_opera),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_opera),
                stringResource(R.string.import_guide_step1_body_desktop_opera),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.VIVALDI -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_vivaldi),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_vivaldi),
                stringResource(R.string.import_guide_step1_body_desktop_vivaldi),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.DUCKDUCKGO -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_duckduckgo),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_duckduckgo),
                stringResource(R.string.import_guide_step1_body_desktop_duckduckgo),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.KIWI -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_kiwi),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_kiwi),
                stringResource(R.string.import_guide_step1_body_desktop_kiwi),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.YANDEX -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_yandex),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_yandex),
                stringResource(R.string.import_guide_step1_body_desktop_yandex),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.ARC -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_arc),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_arc),
                stringResource(R.string.import_guide_step1_body_desktop_arc),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.IE -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_ie),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_ie),
                stringResource(R.string.import_guide_step1_body_desktop_ie),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }

    Browser.UNKNOWN -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_generic, resolvedBrowserName),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_generic, resolvedBrowserName),
                stringResource(R.string.import_guide_step1_body_desktop_generic, resolvedBrowserName),
            ),
            showDesktopGuideButton = hasBookmarkGuideLink(),
        )
    }
}
