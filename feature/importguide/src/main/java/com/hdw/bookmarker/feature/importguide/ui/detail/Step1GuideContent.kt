package com.hdw.bookmarker.feature.importguide.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hdw.bookmarker.core.model.browser.Browser
import com.hdw.bookmarker.core.ui.R

data class Step1GuideContent(
    val step1Title: String,
    val step1Descriptions: Array<String>,
    val showDesktopGuideButton: Boolean,
)

@Composable
fun Browser.toStep1GuideContent(
    resolvedBrowserName: String,
    showDesktopGuideButton: Boolean,
): Step1GuideContent = when (this) {
    Browser.CHROME -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice),
                stringResource(R.string.import_guide_step1_body_desktop),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.BRAVE -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_brave),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_brave),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.EDGE -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_edge),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_edge),
                stringResource(R.string.import_guide_step1_body_desktop_edge),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.NAVER_WHALE -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_naver_whale),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_naver_whale),
                stringResource(R.string.import_guide_step1_body_desktop_naver_whale),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.SAMSUNG_INTERNET -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_samsung_internet),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_samsung_internet),
                stringResource(R.string.import_guide_step1_body_desktop_samsung_internet),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.FIREFOX -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_firefox),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_firefox),
                stringResource(R.string.import_guide_step1_body_desktop_firefox),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.SAFARI -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_safari),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_safari),
                stringResource(R.string.import_guide_step1_body_desktop_safari),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.OPERA -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_opera),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_opera),
                stringResource(R.string.import_guide_step1_body_desktop_opera),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.VIVALDI -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_vivaldi),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_vivaldi),
                stringResource(R.string.import_guide_step1_body_desktop_vivaldi),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.DUCKDUCKGO -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_duckduckgo),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_duckduckgo),
                stringResource(R.string.import_guide_step1_body_desktop_duckduckgo),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.KIWI -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_kiwi),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_kiwi),
                stringResource(R.string.import_guide_step1_body_desktop_kiwi),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.YANDEX -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_yandex),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_yandex),
                stringResource(R.string.import_guide_step1_body_desktop_yandex),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.ARC -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_arc),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_arc),
                stringResource(R.string.import_guide_step1_body_desktop_arc),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.IE -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_ie),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_ie),
                stringResource(R.string.import_guide_step1_body_desktop_ie),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }

    Browser.UNKNOWN -> {
        Step1GuideContent(
            step1Title = stringResource(R.string.import_guide_step1_title_generic, resolvedBrowserName),
            step1Descriptions = arrayOf(
                stringResource(R.string.import_guide_step1_body_notice_generic, resolvedBrowserName),
                stringResource(R.string.import_guide_step1_body_desktop_generic, resolvedBrowserName),
            ),
            showDesktopGuideButton = showDesktopGuideButton,
        )
    }
}
