package com.thezayin.start_up.languages

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.start_up.languages.Language.Companion.default
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

private val LOCALE = 0..1

class LanguageManagerImpl(
    private val context: Context,
    private val json: Json,
    private val preferencesManager: PreferencesManager
) : LanguageManager {

    private val userLanguage = MutableStateFlow(default)
    override val languages: List<Language>
        get() = availableLanguages()

    override fun init(onSuccess: (Language) -> Unit) {
        val locale = getCurrentLocale()
        Log.d("LanguageManagerImpl", "init: $locale")
        val language = languages.first {
            it.locale.value in locale.substring(LOCALE)

        }
        Log.d("LanguageManagerImpl", "init: $language")
        onSuccess(language)
        userLanguage.update { language }
    }

    override fun get(): Flow<Language> = userLanguage

    override fun snapshot(): Language {
        val locale = getCurrentLocale()
        val language = languages.first { it.locale.value in locale.substring(LOCALE) }
        return language
    }

    override fun update(language: Language) {
        val newLocale = language.locale.value
        Log.d("LanguageManagerImpl", "update: $newLocale")
        val locale = LocaleListCompat.forLanguageTags(newLocale).toLanguageTags()
        Log.d("LanguageManagerImpl", "update: $locale")
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(locale))
        preferencesManager.setSelectedLanguage(newLocale)
        userLanguage.update {
            languages.first {
                locale.startsWith(it.locale.value, ignoreCase = true)
            }
        }
        Log.d("LanguageManagerImpl", "update: ${userLanguage.value}")
    }

    private fun getCurrentLocale(): String {
        Log.d(
            "LanguageManagerImpl",
            "getCurrentLocale: ${AppCompatDelegate.getApplicationLocales()}"
        )
        return AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag() ?: default.locale.value
    }

    private fun availableLanguages() = runCatching {
        val languageAssets =
            context.assets.open("locales.json").use { it.readBytes().decodeToString() }
        json.decodeFromString<List<Language>>(languageAssets)
    }.getOrElse { emptyList() }
}

interface LanguageManager {
    val languages: List<Language>
    fun init(onSuccess: (Language) -> Unit)
    fun get(): Flow<Language>
    fun snapshot(): Language
    fun update(language: Language)
}