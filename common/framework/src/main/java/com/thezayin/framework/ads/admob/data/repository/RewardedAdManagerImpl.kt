package com.thezayin.framework.ads.admob.data.repository

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thezayin.framework.ads.admob.domain.repository.RewardedAdManager
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import timber.log.Timber

/**
 * Implementation of the RewardedAdManager interface that handles loading and displaying
 * rewarded ads using Google's AdMob SDK.
 */
class RewardedAdManagerImpl(
    remoteConfig: RemoteConfig, private val preferencesManager: PreferencesManager
) : RewardedAdManager {
    private var rewardedAd: RewardedAd? = null
    private var adId: String = remoteConfig.adUnits.rewardedAd

    /**
     * Load the Rewarded Ad. This method will load the ad only if it is not already loaded.
     * Ad will be loaded asynchronously.
     */
    override fun loadAd(activity: Activity) {
        Timber.tag("RewardedAd").d("Loading rewarded ad")
        if (preferencesManager.isPremiumFlow.value) {
            Timber.tag("RewardedAd").d("App is premium. Skipping loading the ad.")
            return
        }
        if (rewardedAd == null) {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                activity, adId, adRequest, object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        Timber.tag("RewardedAd").d("Rewarded Ad loaded successfully")
                        rewardedAd = ad
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        FirebaseCrashlytics.getInstance()
                            .recordException(Exception("Rewarded Ad failed to load: ${loadAdError.message}"))
                        Timber.tag("RewardedAd")
                            .d("Rewarded Ad failed to load: ${loadAdError.message}")
                        rewardedAd = null
                    }
                })
        }
    }

    /**
     * Show the Rewarded Ad. If the ad is loaded, it will be displayed.
     * Once the ad is dismissed, the next action will be triggered.
     */
    override fun showAd(
        activity: Activity,
        showAd: Boolean,
        adImpression: () -> Unit,
        onReward: (RewardItem) -> Unit,
        onNext: () -> Unit
    ) {
        if (preferencesManager.isPremiumFlow.value) {
            Timber.tag("RewardedAd").d("App is premium. Skipping showing the ad.")
            onNext()
        }
        if (showAd) {
            rewardedAd?.let { ad ->
                ad.show(activity) { rewardItem: RewardItem ->
                    onReward(rewardItem)
                }

                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdImpression() {
                        super.onAdImpression()
                        Timber.tag("RewardedAd").d("Rewarded Ad impression logged")
                        adImpression()
                    }

                    override fun onAdShowedFullScreenContent() {
                        Timber.tag("RewardedAd").d("Rewarded Ad shown")
                        super.onAdShowedFullScreenContent()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        FirebaseCrashlytics.getInstance()
                            .recordException(Exception("Rewarded Ad failed to show: ${adError.message}"))
                        Timber.tag("RewardedAd").d("Rewarded Ad failed to show: ${adError.message}")
                        onNext()
                    }

                    override fun onAdClicked() {
                        Timber.tag("RewardedAd").d("Rewarded Ad clicked")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        Timber.tag("RewardedAd").d("Rewarded Ad dismissed. Reloading the ad.")
                        rewardedAd = null
                        onNext()
                    }
                }
            } ?: run {
                Timber.tag("RewardedAd").d("Rewarded Ad is null. Skipping showing the ad.")
                onNext()
            }
        } else {
            Timber.tag("RewardedAd").d("Rewarded Ad showAd flag is false. Skipping showing the ad.")
            onNext()
        }
    }
}