package com.thezayin.qrscanner.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.thezayin.qrscanner.ui.language.repo.LanguageRepository
import com.thezayin.qrscanner.ui.language.ui.LanguageViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appDi = module {
    singleOf(::AppRestartManagerImpl) bind AppRestartManager::class

    single<SharedPreferences> {
        androidContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    single { LanguageRepository() }
    viewModel {
        LanguageViewModel(
            get(), get(), splitInstallManager = SplitInstallManagerFactory.create(androidContext())
        )
    }
}