package com.thezayin.qrscanner.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.FirebaseApp
import com.thezayin.databases.di.databaseModule
import com.thezayin.framework.di.frameworkModule
import com.thezayin.framework.utils.billing.initializeClient
import com.thezayin.generate.presentation.di.generateModule
import com.thezayin.history.presentation.di.historyModule
import com.thezayin.qrscanner.ui.language.utils.LocaleHelper
import com.thezayin.qrscanner.ui.premium.presentation.di.premiumModule
import com.thezayin.qrscanner.ui.premium.presentation.handler.PremiumStatusInitializer
import com.thezayin.qrscanner.utils.appDi
import com.thezayin.scanner.presentation.di.scannerModule
import com.thezayin.start_up.di.settingsModule
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App : Application() {
    val premiumStatusInitializer: PremiumStatusInitializer by inject()

    private companion object {
        private const val TAG = "jajaApp"
    }

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
                settingsModule,
                premiumModule,
                appDi
            )
        }
        try {
            val localeHelperKoin: LocaleHelper = get()
            localeHelperKoin.applyPersistedLocale()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting/using LocaleHelper from Koin in onCreate.")
        }
        premiumStatusInitializer.checkPremiumStatusOnStartup()
    }

    override fun attachBaseContext(base: Context) {
        val prefs: SharedPreferences = base.getSharedPreferences("app_prefs", MODE_PRIVATE)
        val directLocaleHelper = LocaleHelper(base, prefs)
        val updatedContext = directLocaleHelper.updateContext(base)
        super.attachBaseContext(updatedContext)
    }

}