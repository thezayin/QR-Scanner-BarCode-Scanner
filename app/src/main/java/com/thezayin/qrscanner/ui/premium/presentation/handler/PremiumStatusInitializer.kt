package com.thezayin.qrscanner.ui.premium.presentation.handler

import com.thezayin.framework.preferences.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class PremiumStatusInitializer(
    private val purchaseHandler: PurchaseHandler,
    private val preferencesManager: PreferencesManager
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var initialized = false

    fun checkPremiumStatusOnStartup() {
        if (initialized) {
            Timber.d("PremiumStatusInitializer: Already initialized. Skipping.")
            return
        }
        initialized = true

        Timber.d("PremiumStatusInitializer: Performing initial premium status check.")
        scope.launch {
            purchaseHandler.queryEntitlements()
            val isPremium = preferencesManager.isPremiumFlow.first()
            Timber.d("PremiumStatusInitializer: Initial check complete. User is premium: $isPremium (from prefs)")
        }
    }
}