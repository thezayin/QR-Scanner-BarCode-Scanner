package com.thezayin.qrscanner.utils

import android.app.Application
import android.content.Intent
import com.thezayin.qrscanner.activity.MainActivity
import timber.log.Timber

class AppRestartManagerImpl(
    private val application: Application
) : AppRestartManager {

    override fun restartAppToMainMenu() {
        Timber.d("AppRestartManagerImpl: Received request to restart app to MainActivity.")
        val intent = Intent(application, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        application.startActivity(intent)
        Timber.d("AppRestartManagerImpl: Intent to restart MainActivity dispatched.")
    }
}