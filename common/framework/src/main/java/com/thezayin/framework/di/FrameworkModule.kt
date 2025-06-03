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
import com.thezayin.framework.session.InMemoryScanSessionManager
import com.thezayin.framework.session.ScanSessionManager
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val frameworkModule = module {
    factoryOf(::InterstitialAdManagerImpl) bind InterstitialAdManager::class
    factoryOf(::AppOpenAdManagerImpl) bind AppOpenAdManager::class
    factoryOf(::RewardedAdManagerImpl) bind RewardedAdManager::class
    factoryOf(::InMemoryScanSessionManager) bind ScanSessionManager::class
    factoryOf(::RemoteConfig)
    single { PreferencesManager(get<Application>()) }
}