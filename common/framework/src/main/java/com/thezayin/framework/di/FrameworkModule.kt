package com.thezayin.framework.di

import android.app.Application
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import org.koin.dsl.module

val frameworkModule = module {
    single { PreferencesManager(get<Application>()) }
    single { RemoteConfig(get()) }
    single<com.thezayin.framework.session.ScanSessionManager> { com.thezayin.framework.session.InMemoryScanSessionManager() }
}