package com.thezayin.qrscanner.ui.language.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import timber.log.Timber
import java.util.Locale

class LocaleHelper(private val context: Context, private val prefs: SharedPreferences) {

    companion object {
        private const val SELECTED_LANGUAGE_KEY = "selected_language"
        const val DEFAULT_LANGUAGE = "en"
        private const val TAG = "LocaleHelper"
    }

    fun setLocale(languageCode: String) {
        Timber.tag(TAG).d("Attempting to persist language: $languageCode")
        persistLanguage(languageCode)
        updateLocaleConfiguration(languageCode)
    }

    private fun persistLanguage(languageCode: String) {
        val success = prefs.edit().putString(SELECTED_LANGUAGE_KEY, languageCode).commit()
        if (success) {
            Timber.tag(TAG)
                .i("Successfully committed language '$languageCode' to SharedPreferences key: $SELECTED_LANGUAGE_KEY.")
        } else {
            Timber.tag(TAG)
                .e("Failed to commit language '$languageCode' to SharedPreferences key: $SELECTED_LANGUAGE_KEY.")
        }
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
        val prefsForAttach = baseContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val languageCode =
            prefsForAttach.getString(SELECTED_LANGUAGE_KEY, null) ?: getDeviceLocaleCode()
        Timber.tag(TAG).d("updateContext: Using language code '$languageCode' for new context.")
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val configuration = Configuration(baseContext.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            baseContext.createConfigurationContext(configuration)
        } else {
            @Suppress("DEPRECATION") baseContext.resources.updateConfiguration(
                configuration, baseContext.resources.displayMetrics
            )
            baseContext
        }
    }
}