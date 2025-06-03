package com.thezayin.qrscanner.ui.language.utils

fun getCountryCodeForLanguage(languageCode: String): String {
    return when (languageCode.lowercase()) {
        "en" -> "US"
        "fr" -> "FR"
        "es" -> "ES"
        "ar" -> "SA"
        "ru" -> "RU"
        "pt" -> "PT"
        "hi" -> "IN"
        "da" -> "DK"
        "it" -> "IT"
        "tr" -> "TR"
        "id" -> "ID"
        "ja" -> "JP"
        "ko" -> "KR"
        "pl" -> "PL"
        "af" -> "ZA"
        "zh" -> "CN"
        "zh-tw" -> "TW"
        "th" -> "TH"
        "fa" -> "IR"
        "vi" -> "VN"
        "hu" -> "HU"
        "he" -> "IL"
        "sv" -> "SE"
        "no" -> "NO"
        "ca" -> "ES"
        "ms" -> "MY"
        "nl" -> "NL"
        "cs" -> "CZ"
        "ur" -> "PK"
        "de" -> "DE"
        "uk" -> "UA"
        "bn" -> "BD"
        "hy" -> "AM"
        "ro" -> "RO"
        "lo" -> "LA"
        else -> "US"
    }
}

fun countryCodeToFlagEmoji(countryCode: String): String {
    if (countryCode.length != 2 || !countryCode.all { it.isLetter() }) {
        return "🏳️"
    }
    val upperCountryCode = countryCode.uppercase()
    val firstChar = upperCountryCode[0].code - 'A'.code + 0x1F1E6
    val secondChar = upperCountryCode[1].code - 'A'.code + 0x1F1E6
    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}