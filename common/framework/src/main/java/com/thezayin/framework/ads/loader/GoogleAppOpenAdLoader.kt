package com.thezayin.framework.ads.loader

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics

object GoogleAppOpenAdLoader {
    fun loadAd(
        context: Context,
        adUnitId: String,
        onAdLoaded: (AppOpenAd) -> Unit,
        onAdLoading: () -> Unit,
        onAdFailed: () -> Unit
    ) {
        onAdLoading()
        AppOpenAd.load(
            context, adUnitId, AdRequest.Builder().build(),
            object : AppOpenAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    FirebaseCrashlytics.getInstance()
                        .recordException(Exception("Ad failed to load: $loadAdError"))
                    onAdFailed()
                    FirebaseCrashlytics.getInstance().log("Ad failed to load: $loadAdError")
                }

                override fun onAdLoaded(appOpenAd: AppOpenAd) {
                    onAdLoaded(appOpenAd)
                }
            }
        )
    }
}