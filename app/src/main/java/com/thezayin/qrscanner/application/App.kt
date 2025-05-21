package com.thezayin.qrscanner.application

import android.app.Application
import com.google.firebase.FirebaseApp
import com.thezayin.databases.di.databaseModule
import com.thezayin.framework.di.frameworkModule
import com.thezayin.framework.utils.billing.initializeBilling
import com.thezayin.framework.utils.billing.initializeClient
import com.thezayin.generate.presentation.di.generateModule
import com.thezayin.history.presentation.di.historyModule
import com.thezayin.scanner.presentation.di.scannerModule
import com.thezayin.start_up.di.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeClient(this)
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
                settingsModule
            )
        }
    }
}