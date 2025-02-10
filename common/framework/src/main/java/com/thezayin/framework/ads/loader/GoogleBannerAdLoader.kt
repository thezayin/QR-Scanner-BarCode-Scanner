package com.thezayin.framework.ads.loader

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

object GoogleBannerAdLoader {
    private const val DEFAULT_BANNER_AD_UNIT = "ca-app-pub-3940256099942544/6300978111"
    fun getBannerAd(context: Context, adUnit: String): AdView {
        val finalAdUnit = adUnit.ifBlank { DEFAULT_BANNER_AD_UNIT }
        val adView = AdView(context).apply {
            setAdSize(AdSize.BANNER)
            val adRequest = AdRequest.Builder().build()
            adUnitId = finalAdUnit
            loadAd(adRequest)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.d("AdView", "Ad loaded successfully.")
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    Log.d("AdView", "Ad failed to load with error code: $p0")
                }
            }
        }
        return adView
    }
}