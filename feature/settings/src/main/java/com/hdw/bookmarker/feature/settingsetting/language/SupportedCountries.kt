package com.hdw.bookmarker.feature.settingsetting.language

enum class SupportedCountries(val language: SupportedLanguageTags) {
    GLOBAL_ENGLISH(SupportedLanguageTags.EN),
    INDIA(SupportedLanguageTags.EN_IN),
    KOREA(SupportedLanguageTags.KO),
    JAPAN(SupportedLanguageTags.JA),
    FRANCE(SupportedLanguageTags.FR),
    GERMANY(SupportedLanguageTags.DE),
    ITALY(SupportedLanguageTags.IT),
    TURKIYE(SupportedLanguageTags.TR),
    SPAIN(SupportedLanguageTags.ES),
    BRAZIL(SupportedLanguageTags.PT_BR),
    PORTUGAL(SupportedLanguageTags.PT_PT),
    INDIA_HINDI(SupportedLanguageTags.HI),
    INDONESIA(SupportedLanguageTags.ID),
    RUSSIA(SupportedLanguageTags.RU),
    CHINA(SupportedLanguageTags.ZH_CN),
    HONG_KONG(SupportedLanguageTags.ZH_HK),
    TAIWAN(SupportedLanguageTags.ZH_TW),
    THAILAND(SupportedLanguageTags.TH),
    VIETNAM(SupportedLanguageTags.VI),
    ;

    companion object {
        val supportedLanguageTags: List<String> by lazy { entries.map { it.language.tag } }
    }
}

enum class SupportedLanguageTags(val tag: String) {
    EN("en"),
    EN_IN("en-IN"),
    KO("ko"),
    JA("ja"),
    FR("fr"),
    DE("de"),
    IT("it"),
    TR("tr"),
    ES("es"),
    PT_BR("pt-BR"),
    PT_PT("pt-PT"),
    HI("hi"),
    ID("id"),
    RU("ru"),
    ZH_CN("zh-CN"),
    ZH_HK("zh-HK"),
    ZH_TW("zh-TW"),
    TH("th"),
    VI("vi"),
}
