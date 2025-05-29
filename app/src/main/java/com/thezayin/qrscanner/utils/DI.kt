package com.thezayin.qrscanner.utils

import android.content.Context
import android.content.SharedPreferences
import com.thezayin.qrscanner.ui.language.model.LanguageItem
import com.thezayin.qrscanner.ui.language.ui.LanguageViewModel
import com.thezayin.qrscanner.ui.language.utils.LocaleHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appDi = module {
    singleOf(::AppRestartManagerImpl) bind AppRestartManager::class

    single<SharedPreferences> {
        androidContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    single {
        LocaleHelper(androidContext(), get())
    }

    factory {
        listOf(
            LanguageItem(code = "en", displayName = "English"),
            LanguageItem(code = "fr", displayName = "French (Français)"),
            LanguageItem(code = "es", displayName = "Spanish (Español)"),
            LanguageItem(
                code = "ar", displayName = "Arabic (العربیتہ)"
            ),
            LanguageItem(code = "ru", displayName = "Russian (Русский)"),
            LanguageItem(code = "pt", displayName = "Portuguese (Português)"),
            LanguageItem(code = "hi", displayName = "Hindi (हिंदी)"),
            LanguageItem(code = "da", displayName = "Danish (Dansk)"),
            LanguageItem(code = "it", displayName = "Italian (Italiano)"),
            LanguageItem(
                code = "tr", displayName = "Turkish (Türkçe)"
            ),
            LanguageItem(code = "id", displayName = "Indonesian (Bahasa Indonesia)"),
            LanguageItem(code = "ja", displayName = "Japanese (日本語)"),
            LanguageItem(code = "ko", displayName = "Korean (한국어)"),
            LanguageItem(code = "pl", displayName = "Polish (Polskie)"),
            LanguageItem(code = "af", displayName = "Afrikaans (Afrikaanse)"),
            LanguageItem(code = "zh", displayName = "Chinese (简体中文)"),
            LanguageItem(
                code = "zh-TW", displayName = "Chinese Traditional (繁體中文)"
            ),
            LanguageItem(code = "th", displayName = "Thai (ไทย)"),
            LanguageItem(code = "fa", displayName = "Persian (فارسی)"),
            LanguageItem(
                code = "vi", displayName = "Vietnamese (Tiếng Việt)"
            ),
            LanguageItem(code = "hu", displayName = "Hungarian (Magyar)"),
            LanguageItem(code = "he", displayName = "Hebrew (עברית)"),
            LanguageItem(code = "sv", displayName = "Swedish (Svenska)"),
            LanguageItem(code = "no", displayName = "Norwegian (Norsk)"),
            LanguageItem(code = "ca", displayName = "Catalan (Catalana)"),
            LanguageItem(code = "ms", displayName = "Malay (Melayu)"),
            LanguageItem(code = "nl", displayName = "Dutch (Nederlands)"),
            LanguageItem(
                code = "cs", displayName = "Czech (Čeština)"
            ),
            LanguageItem(code = "ur", displayName = "Urdu (اردو)"),
            LanguageItem(code = "de", displayName = "German (Deutsch)"),
            LanguageItem(code = "uk", displayName = "Ukrainian (українська)"),
            LanguageItem(code = "bn", displayName = "Bengali (বাংলা)"),
            LanguageItem(code = "hy", displayName = "Armenian (Հայերեն)"),
            LanguageItem(code = "ro", displayName = "Romanian (Română)")
        )
    }

    viewModelOf(::LanguageViewModel)
}