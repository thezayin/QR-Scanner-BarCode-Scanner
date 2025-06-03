package com.thezayin.framework.ads.admob.data.repository

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thezayin.framework.ads.admob.domain.repository.InterstitialAdManager
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import timber.log.Timber

/**
 * Implementation of the InterstitialAdManager interface to manage the loading, showing,
 * and handling of interstitial ads using Google's AdMob SDK.
 */
class InterstitialAdManagerImpl(
    remoteConfig: RemoteConfig, private val preferencesManager: PreferencesManager
) : InterstitialAdManager {

    private var interstitialAd: InterstitialAd? = null
    private var adId: String = remoteConfig.adUnits.interstitialAd

    /**
     * Loads the interstitial ad if it's not already loaded.
     * This method ensures that the ad is only loaded once and does not reload until necessary.
     */
    override fun loadAd(
        activity: Activity
    ) {
        if (preferencesManager.isPremiumFlow.value) {
            Timber.tag("InterstitialAd").d("App is premium. Skipping showing the ad.")
            return
        }
        if (interstitialAd == null) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                activity, adId, adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        Timber.tag("InterstitialAd").d("Ad loaded successfully")
                        interstitialAd = ad
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        FirebaseCrashlytics.getInstance()
                            .recordException(Exception("Ad failed to load: ${adError.message}"))
                        Timber.tag("InterstitialAd").d("Ad failed to load: ${adError.message}")
                        interstitialAd = null
                    }
                })
        }
    }

    /**
     * Displays the interstitial ad if it's loaded. It triggers the appropriate callbacks
     * based on whether the ad was successfully displayed or failed to show.
     */
    override fun showAd(
        activity: Activity, showAd: Boolean, adImpression: () -> Unit, onNext: () -> Unit
    ) {
        if (preferencesManager.isPremiumFlow.value) {
            Timber.tag("InterstitialAd").d("App is premium. Skipping showing the ad.")
            onNext()
        }
        if (showAd) {
            interstitialAd?.let { ad ->
                ad.show(activity)

                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdImpression() {
                        Timber.tag("InterstitialAd").d("Ad impression logged")
                        adImpression()
                    }

                    override fun onAdShowedFullScreenContent() {
                        Timber.tag("InterstitialAd").d("Ad shown")
                        super.onAdShowedFullScreenContent()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        FirebaseCrashlytics.getInstance()
                            .recordException(Exception("Ad failed to show: ${adError.message}"))
                        Timber.tag("InterstitialAd").d("Ad failed to show: ${adError.message}")
                        onNext()
                    }

                    override fun onAdClicked() {
                        Timber.tag("InterstitialAd").d("Ad clicked")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        Timber.tag("InterstitialAd").d("Ad dismissed. Reloading the ad.")
                        interstitialAd = null
                        onNext()
                    }
                }
            } ?: run {
                Timber.tag("InterstitialAd").d("Ad is null. Skipping showing the ad.")
                onNext()
            }
        } else {
            Timber.tag("InterstitialAd").d("Ad showAd flag is false. Skipping showing the ad.")
            onNext()
        }
    }
}