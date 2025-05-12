package com.thezayin.framework.ads.admob.data.repository

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.thezayin.framework.ads.admob.domain.repository.AppOpenAdManager
import com.thezayin.framework.remote.RemoteConfig
import timber.log.Timber

/**
 * Implementation of the AppOpenAdManager interface to manage the loading, showing,
 * and handling of App Open ads using Google's AdMob SDK.
 */
class AppOpenAdManagerImpl(
    remoteConfig: RemoteConfig,
) : AppOpenAdManager {

    // Holds the loaded App Open ad instance
    private var appOpenAd: AppOpenAd? = null

    // Ad Unit ID (replace with your actual AdMob App Open Ad Unit ID)
    private val adId: String = remoteConfig.adUnits.appOpenAd

    /**
     * Loads the App Open ad if it's not already loaded.
     * This method ensures that the ad is only loaded once and doesn't reload it until necessary.
     */
    override fun loadAd(activity: Activity) {
        Timber.tag("AppOpenAd").d("Loading app open ad")

        // Only load the ad if it's not already loaded
        if (appOpenAd == null) {
            // Create an ad request to load the ad
            val adRequest = AdRequest.Builder().build()

            // Request to load the App Open ad
            AppOpenAd.load(
                activity, // The activity where the ad will be displayed
                adId,     // The ad unit ID for the App Open ad
                adRequest, // The ad request to load the ad
                object : AppOpenAd.AppOpenAdLoadCallback() { // Callback for handling load result
                    // Called when the ad is successfully loaded
                    override fun onAdLoaded(ad: AppOpenAd) {
                        Timber.tag("AppOpenAd").d("App Open Ad loaded successfully")
                        appOpenAd = ad // Save the loaded ad for later use
                    }

                    // Called when the ad fails to load
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Timber.tag("AppOpenAd")
                            .d("App Open Ad failed to load: ${loadAdError.message}")
                        appOpenAd = null // Reset the ad if loading fails
                    }
                })
        }
    }

    /**
     * Displays the App Open ad if it's loaded. It triggers the appropriate callbacks
     * based on whether the ad was successfully displayed or failed to show.
     */
    override fun showAd(
        activity: Activity,  // The activity where the ad will be shown
        showAd: Boolean,     // Whether or not to show the ad
        adImpression: () -> Unit, // Callback to handle ad impression
        onNext: () -> Unit   // Callback to trigger the next action after the ad is dismissed or fails to show
    ) {
        // Check if we need to show the ad
        if (showAd) {
            // Check if the ad is loaded
            appOpenAd?.let { ad -> // If the ad is loaded, show it
                ad.show(activity) // Show the App Open ad

                // FullScreenContentCallback to handle the lifecycle of the ad
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    // Called when the ad impression is logged (i.e., when it's shown)
                    override fun onAdImpression() {
                        adImpression()  // Trigger ad impression callback
                    }

                    // Called when the ad is shown successfully
                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                    }

                    // Called if the ad fails to show (e.g., no ad available)
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Timber.tag("AppOpenAd").d("App Open Ad failed to show: ${adError.message}")
                        onNext() // Proceed to the next action if the ad fails to show
                    }

                    // Optional: Called when the ad is clicked by the user
                    override fun onAdClicked() {
                        // Handle ad click, if needed
                    }

                    // Called when the ad is dismissed by the user (either by clicking 'close' or after the ad ends)
                    override fun onAdDismissedFullScreenContent() {
                        onNext() // Trigger the next action after the ad is dismissed
                    }
                }
            } ?: run {
                // If the ad isn't loaded yet, skip showing the ad and proceed with the next action
                onNext()
            }
        } else {
            // If the showAd flag is false, skip the ad and proceed to the next action
            onNext()
        }
    }
}
