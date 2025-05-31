// com.thezayin.qrscanner.ui.language.utils.LocaleHelper.kt
package com.thezayin.qrscanner.ui.language.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import java.util.Locale

class LocaleHelper(private val context: Context, private val prefs: SharedPreferences) {

    companion object {
        private const val SELECTED_LANGUAGE_KEY = "selected_language"
        const val DEFAULT_LANGUAGE = "en"
    }

    fun setLocale(languageCode: String) {
        persistLanguage(languageCode)
        updateLocaleConfiguration(languageCode)
    }

    private fun persistLanguage(languageCode: String) {
        prefs.edit().putString(SELECTED_LANGUAGE_KEY, languageCode).apply()
    }

    fun getPersistedLocaleCode(): String {
        val savedLang = prefs.getString(SELECTED_LANGUAGE_KEY, null)
        return savedLang ?: getDeviceLocaleCode()
    }

    private fun getDeviceLocaleCode(): String {
        val deviceLocale =
            ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]?.language
                ?: DEFAULT_LANGUAGE
        return deviceLocale
    }

    fun applyPersistedLocale() {
        val localeCode = getPersistedLocaleCode()
        updateLocaleConfiguration(localeCode)
    }

    private fun updateLocaleConfiguration(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val appLocale = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        } else {
            val resources: Resources = context.resources
            val configuration: Configuration = resources.configuration
            configuration.setLocale(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLayoutDirection(locale)
            }
            @Suppress("DEPRECATION") resources.updateConfiguration(
                configuration, resources.displayMetrics
            )
        }
    }

    fun updateContext(baseContext: Context): Context {
        val currentPrefs = baseContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val languageCode =
            currentPrefs.getString(SELECTED_LANGUAGE_KEY, null) ?: getDeviceLocaleCode()
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val configuration = Configuration(baseContext.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            baseContext.createConfigurationContext(configuration)
        } else {
            // FIX: Use baseContext.resources.displayMetrics instead of baseContext.displayMetrics
            @Suppress("DEPRECATION") baseContext.resources.updateConfiguration(
                configuration, baseContext.resources.displayMetrics // <--- FIXED LINE
            )
            baseContext
        }
    }
}