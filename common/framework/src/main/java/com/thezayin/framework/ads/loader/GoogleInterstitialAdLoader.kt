package com.thezayin.framework.ads.loader

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics

object GoogleInterstitialAdLoader {
    fun loadAd(
        context: Context,
        adUnitId: String,
        onAdLoaded: (InterstitialAd) -> Unit,
        onAdLoading: () -> Unit,
        onAdFailed: () -> Unit
    ) {
        onAdLoading()
        InterstitialAd.load(
            context, adUnitId, AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e("InterstitialAd", "Ad failed to load: $loadAdError")
                    FirebaseCrashlytics.getInstance()
                        .recordException(Exception("Ad failed to load: $loadAdError"))
                    onAdFailed()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    onAdLoaded(interstitialAd)
                }
            }
        )
    }
}