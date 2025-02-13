package com.thezayin.start_up.languages.utils

fun countryCodeToFlagEmoji(countryCode: String): String {
    if (countryCode.length != 2) return countryCode
    val firstChar = countryCode[0].uppercaseChar() - 'A' + 0x1F1E6
    val secondChar = countryCode[1].uppercaseChar() - 'A' + 0x1F1E6
    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}

/**
 * Maps a language code to a country code for displaying the flag.
 */
fun languageCodeToCountryCode(languageCode: String): String {
    return when (languageCode.lowercase()) {
        "en" -> "US"
        "fr" -> "FR"
        "es" -> "ES"
        "ar" -> "SA"
        "ru" -> "RU"
        "pt" -> "BR"
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
        else -> "US"
    }
}