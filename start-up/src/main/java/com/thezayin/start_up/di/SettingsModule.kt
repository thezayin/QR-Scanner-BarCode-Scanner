package com.thezayin.start_up.di

import com.thezayin.start_up.onboarding.OnboardingViewModel
import com.thezayin.start_up.setting.SettingsViewModel
import com.thezayin.start_up.splash.SplashViewModel
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }


    viewModelOf(::SplashViewModel)
    viewModelOf(::OnboardingViewModel)
}
