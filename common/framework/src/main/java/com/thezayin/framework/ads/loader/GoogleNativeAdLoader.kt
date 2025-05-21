package com.thezayin.framework.ads.loader

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import timber.log.Timber

object GoogleNativeAdLoader {
    fun loadNativeAd(
        context: Context,
        adUnitId: String,
        onNativeAdLoaded: (NativeAd) -> Unit
    ) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { nativeAd ->
                Timber.tag("NativeAdBuilder").d("Native ad loaded successfully.")
                onNativeAdLoaded(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Timber.tag("NativeAdBuilder")
                        .d("Native ad failed to load: ${loadAdError.message}, Error code: ${loadAdError.code}, Response info: ${loadAdError.responseInfo}")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }
}