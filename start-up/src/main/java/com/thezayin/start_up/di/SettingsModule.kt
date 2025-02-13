package com.thezayin.start_up.di

import com.thezayin.start_up.languages.manager.LanguageManager
import com.thezayin.start_up.languages.manager.LanguageManagerImpl
import com.thezayin.start_up.languages.LanguageViewModel
import com.thezayin.start_up.onboarding.OnboardingViewModel
import com.thezayin.start_up.setting.SettingsViewModel
import com.thezayin.start_up.splash.SplashViewModel
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

    singleOf(::LanguageManagerImpl) bind LanguageManager::class
    viewModelOf(::LanguageViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::OnboardingViewModel)
}
