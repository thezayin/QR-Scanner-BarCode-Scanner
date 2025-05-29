package com.thezayin.framework.ads.loader

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thezayin.framework.preferences.PreferencesManager
import timber.log.Timber

object GoogleNativeAdLoader {
    fun loadNativeAd(
        preferencesManager: PreferencesManager,
        context: Context,
        adUnitId: String,
        onNativeAdLoaded: (NativeAd) -> Unit
    ) {
        if (preferencesManager.isPremiumFlow.value) return

        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                Timber.tag("NativeAdBuilder").d("Native ad loaded successfully.")
                onNativeAdLoaded(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    FirebaseCrashlytics.getInstance()
                        .recordException(Exception("Native ad failed to load: ${loadAdError.message}"))
                    Timber.tag("NativeAdBuilder")
                        .d("Native ad failed to load: ${loadAdError.message}, Error code: ${loadAdError.code}, Response info: ${loadAdError.responseInfo}")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }
}