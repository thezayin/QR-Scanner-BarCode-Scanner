package com.thezayin.framework.ads.loader

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics

object GoogleRewardedAdLoader {
    fun loadAd(
        context: Context,
        adUnitId: String,
        onAdLoaded: (RewardedAd) -> Unit,
        onAdLoading: () -> Unit,
        onAdFailed: () -> Unit
    ) {
        onAdLoading()
        RewardedAd.load(
            context, adUnitId, AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    FirebaseCrashlytics.getInstance()
                        .recordException(Exception("Ad failed to load: $loadAdError"))
                    onAdFailed()
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    onAdLoaded(rewardedAd)
                }
            }
        )
    }
}