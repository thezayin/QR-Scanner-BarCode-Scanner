package com.thezayin.framework.di

import android.app.Application
import com.thezayin.framework.ads.admob.data.repository.AppOpenAdManagerImpl
import com.thezayin.framework.ads.admob.data.repository.InterstitialAdManagerImpl
import com.thezayin.framework.ads.admob.data.repository.RewardedAdManagerImpl
import com.thezayin.framework.ads.admob.domain.repository.AppOpenAdManager
import com.thezayin.framework.ads.admob.domain.repository.InterstitialAdManager
import com.thezayin.framework.ads.admob.domain.repository.RewardedAdManager
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import org.koin.dsl.module

val frameworkModule = module {
    single<InterstitialAdManager> { InterstitialAdManagerImpl(get()) }
    single<AppOpenAdManager> { AppOpenAdManagerImpl(get()) }
    single<RewardedAdManager> { RewardedAdManagerImpl(get()) }
    single { PreferencesManager(get<Application>()) }
    single { RemoteConfig(get()) }
    single<com.thezayin.framework.session.ScanSessionManager> { com.thezayin.framework.session.InMemoryScanSessionManager() }
}