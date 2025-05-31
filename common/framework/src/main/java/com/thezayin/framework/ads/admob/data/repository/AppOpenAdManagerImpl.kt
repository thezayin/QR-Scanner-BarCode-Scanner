package com.thezayin.framework.ads.admob.data.repository

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thezayin.framework.ads.admob.domain.repository.AppOpenAdManager
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import timber.log.Timber

/**
 * Implementation of the AppOpenAdManager interface to manage the loading, showing,
 * and handling of App Open ads using Google's AdMob SDK.
 */
class AppOpenAdManagerImpl(
    remoteConfig: RemoteConfig,
    val preferencesManager: PreferencesManager
) : AppOpenAdManager {
    private var appOpenAd: AppOpenAd? = null
    private val adId: String = remoteConfig.adUnits.appOpenAd

    /**
     * Loads the App Open ad if it's not already loaded.
     * This method ensures that the ad is only loaded once and doesn't reload it until necessary.
     */
    override fun loadAd(activity: Activity) {
        Timber.tag("AppOpenAd").d("Loading app open ad")
        if (preferencesManager.isPremiumFlow.value) {
            Timber.tag("AppOpenAd").d("App is premium. Skipping showing the ad.")
            return
        }

        if (appOpenAd == null) {
            val adRequest = AdRequest.Builder().build()
            AppOpenAd.load(
                activity,
                adId,
                adRequest,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        Timber.tag("AppOpenAd").d("App Open Ad loaded successfully")
                        appOpenAd = ad
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        FirebaseCrashlytics.getInstance()
                            .recordException(Exception("App Open Ad failed to load: ${loadAdError.message}"))
                        Timber.tag("AppOpenAd")
                            .d("App Open Ad failed to load: ${loadAdError.message}")
                        appOpenAd = null
                    }
                })
        }
    }

    /**
     * Displays the App Open ad if it's loaded. It triggers the appropriate callbacks
     * based on whether the ad was successfully displayed or failed to show.
     */
    override fun showAd(
        activity: Activity,
        showAd: Boolean,
        adImpression: () -> Unit,
        onNext: () -> Unit
    ) {
        if (preferencesManager.isPremiumFlow.value) {
            Timber.tag("AppOpenAd").d("App is premium. Skipping showing the ad.")
            onNext()
        }
        if (showAd) {
            appOpenAd?.let { ad ->
                ad.show(activity)
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdImpression() {
                        Timber.tag("AppOpenAd").d("App Open Ad impression logged")
                        adImpression()
                    }

                    override fun onAdShowedFullScreenContent() {
                        Timber.tag("AppOpenAd").d("App Open Ad shown")
                        super.onAdShowedFullScreenContent()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        FirebaseCrashlytics.getInstance()
                            .recordException(Exception("App Open Ad failed to show: ${adError.message}"))
                        Timber.tag("AppOpenAd").d("App Open Ad failed to show: ${adError.message}")
                        onNext()
                    }

                    override fun onAdClicked() {
                        Timber.tag("AppOpenAd").d("App Open Ad clicked")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        Timber.tag("AppOpenAd").d("App Open Ad dismissed. Reloading the ad.")
                        onNext()
                    }
                }
            } ?: run {
                Timber.tag("AppOpenAd").d("App Open Ad is null. Skipping showing the ad.")
                onNext()
            }
        } else {
            Timber.tag("AppOpenAd").d("App Open Ad showAd flag is false. Skipping showing the ad.")
            onNext()
        }
    }
}
