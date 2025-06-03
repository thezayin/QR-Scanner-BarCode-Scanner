package com.thezayin.qrscanner.ui.language.repo

import com.thezayin.framework.preferences.Language
import com.thezayin.qrscanner.ui.language.utils.countryCodeToFlagEmoji
import com.thezayin.qrscanner.ui.language.utils.getCountryCodeForLanguage
import java.util.Locale

class LanguageRepository() {

    fun getAvailableLanguages(): List<Language> {
        val languageCodeList = listOf(
            "en",
            "es",
            "fr",
            "ar",
            "ru",
            "pt",
            "hi",
            "da",
            "it",
            "tr",
            "in",
            "ja",
            "ko",
            "pl",
            "af",
            "zh",
            "zh-rTW",
            "th",
            "fa",
            "vi",
            "hu",
            "iw",
            "sv",
            "no",
            "ca",
            "ms",
            "nl",
            "cs",
            "ur",
            "de",
            "uk",
            "bn",
            "hy",
            "ro"
        )

        val languages = languageCodeList.map { langCode ->
            val locale = Locale.forLanguageTag(langCode)
            val localizedName = locale.getDisplayName(Locale.getDefault())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            val nativeName = locale.getDisplayName(locale)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
            val displayString = if (localizedName != nativeName) {
                "$localizedName ($nativeName)"
            } else {
                localizedName
            }

            val countryCode = getCountryCodeForLanguage(langCode)
            val flag = countryCodeToFlagEmoji(countryCode)
            Language(langCode, displayString, flag)
        }
        val systemLocale = Locale.getDefault()
        val systemLanguageName = systemLocale.getDisplayName(systemLocale)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(systemLocale) else it.toString() }

        return listOf(
            Language(
                Language.SYSTEM_DEFAULT_CODE, "System Language ($systemLanguageName)", "🌐"
            )
        ) + languages
    }
}