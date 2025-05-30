package com.thezayin.qrscanner.ui.language.utils

/**
 * Maps a language code to a country code for displaying the flag.
 * Note: This is a simplified mapping and might not cover all regional variations
 * or political sensitivities. For "ca" (Catalan), "ES" (Spain) is used as an example.
 */
fun languageCodeToCountryCode(languageCode: String): String {
    return when (languageCode.lowercase()) {
        "en" -> "US" // English - United States
        "fr" -> "FR" // French - France
        "es" -> "ES" // Spanish - Spain
        "ar" -> "SA" // Arabic - Saudi Arabia
        "ru" -> "RU" // Russian - Russia
        "pt" -> "PT" // Portuguese - Portugal (could also be BR for Brazil)
        "hi" -> "IN" // Hindi - India
        "da" -> "DK" // Danish - Denmark
        "it" -> "IT" // Italian - Italy
        "tr" -> "TR" // Turkish - Turkey
        "id" -> "ID" // Indonesian - Indonesia
        "ja" -> "JP" // Japanese - Japan
        "ko" -> "KR" // Korean - South Korea
        "pl" -> "PL" // Polish - Poland
        "af" -> "ZA" // Afrikaans - South Africa
        "zh" -> "CN" // Chinese (Simplified) - China
        "zh-tw" -> "TW" // Chinese (Traditional) - Taiwan
        "th" -> "TH" // Thai - Thailand
        "fa" -> "IR" // Persian (Farsi) - Iran
        "vi" -> "VN" // Vietnamese - Vietnam
        "hu" -> "HU" // Hungarian - Hungary
        "he" -> "IL" // Hebrew - Israel
        "sv" -> "SE" // Swedish - Sweden
        "no" -> "NO" // Norwegian - Norway
        "ca" -> "ES" // Catalan - Spain (Region of Catalonia)
        "ms" -> "MY" // Malay - Malaysia
        "nl" -> "NL" // Dutch - Netherlands
        "cs" -> "CZ" // Czech - Czech Republic
        "ur" -> "PK" // Urdu - Pakistan
        "de" -> "DE" // German - Germany
        "uk" -> "UA" // Ukrainian - Ukraine
        "bn" -> "BD" // Bengali - Bangladesh
        "hy" -> "AM" // Armenian - Armenia
        "ro" -> "RO" // Romanian - Romania
        // Add more mappings as needed
        else -> "US" // Default to US flag or a globe emoji "🌐" if preferred
    }
}

/**
 * Converts a two-letter ISO 3166-1 alpha-2 country code to a flag emoji.
 */
fun countryCodeToFlagEmoji(countryCode: String): String {
    if (countryCode.length != 2 || !countryCode.all { it.isLetter() && it.isUpperCase() }) {
        return "🏳️"
    }
    val firstChar = countryCode[0].code - 'A'.code + 0x1F1E6
    val secondChar = countryCode[1].code - 'A'.code + 0x1F1E6
    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}