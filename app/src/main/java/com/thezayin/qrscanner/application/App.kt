package com.thezayin.qrscanner.application

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.thezayin.databases.di.databaseModule
import com.thezayin.framework.di.frameworkModule
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.generate.presentation.di.generateModule
import com.thezayin.history.presentation.di.historyModule
import com.thezayin.qrscanner.ui.premium.presentation.di.premiumModule
import com.thezayin.qrscanner.ui.premium.presentation.handler.PremiumStatusInitializer
import com.thezayin.qrscanner.utils.appDi
import com.thezayin.scanner.presentation.di.scannerModule
import com.thezayin.start_up.di.settingsModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App : Application() {
    val premiumStatusInitializer: PremiumStatusInitializer by inject()

    private val preferencesManager: PreferencesManager by inject()
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(
                frameworkModule,
                databaseModule,
                scannerModule,
                historyModule,
                generateModule,
                settingsModule,
                premiumModule,
                appDi
            )
        }
        preferencesManager.initializeLocale()
        premiumStatusInitializer.checkPremiumStatusOnStartup()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }
}