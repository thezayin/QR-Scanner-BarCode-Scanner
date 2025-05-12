package com.thezayin.framework.ads.admob.data.repository

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.thezayin.framework.ads.admob.domain.repository.InterstitialAdManager
import com.thezayin.framework.remote.RemoteConfig
import timber.log.Timber

/**
 * Implementation of the InterstitialAdManager interface to manage the loading, showing,
 * and handling of interstitial ads using Google's AdMob SDK.
 */
class InterstitialAdManagerImpl(
    private val remoteConfig: RemoteConfig
) : InterstitialAdManager {

    // Holds the loaded Interstitial ad instance
    private var interstitialAd: InterstitialAd? = null

    // Ad Unit ID (replace with your actual AdMob Interstitial Ad Unit ID)
    private var adId: String = remoteConfig.adUnits.interstitialAd

    /**
     * Loads the interstitial ad if it's not already loaded.
     * This method ensures that the ad is only loaded once and does not reload until necessary.
     */
    override fun loadAd(
        activity: Activity
    ) {
        // Only load the ad if it's not already loaded
        if (interstitialAd == null) {
            // Create an ad request and load the interstitial ad
            val adRequest = AdRequest.Builder().build()

            // Request to load the interstitial ad
            InterstitialAd.load(
                activity, // The activity where the ad will be displayed
                adId,     // The ad unit ID for the interstitial ad
                adRequest, // The ad request to be sent to load the ad
                object : InterstitialAdLoadCallback() { // Callback for handling loading result
                    // Called when the ad is successfully loaded
                    override fun onAdLoaded(ad: InterstitialAd) {
                        Log.d("InterstitialAd", "Ad loaded successfully")
                        Timber.tag("InterstitialAd").d("Ad loaded successfully")
                        interstitialAd = ad // Save the loaded ad
                    }

                    // Called if the ad fails to load
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.e("InterstitialAd", "Ad failed to load: ${adError.message}")
                        Timber.tag("InterstitialAd").d("Ad failed to load: ${adError.message}")
                        interstitialAd = null // Reset the ad if loading fails
                    }
                })
        }
    }

    /**
     * Displays the interstitial ad if it's loaded. It triggers the appropriate callbacks
     * based on whether the ad was successfully displayed or failed to show.
     */
    override fun showAd(
        activity: Activity,  // The activity where the ad will be shown
        showAd: Boolean,     // Whether or not to show the ad
        adImpression: () -> Unit, // Callback to handle ad impression
        onNext: () -> Unit   // Callback to trigger the next action after the ad is dismissed or fails
    ) {
        // Check if we need to show the ad
        if (showAd) {
            // Check if the ad is loaded
            interstitialAd?.let { ad -> // If the ad is loaded, show it
                ad.show(activity) // Show the interstitial ad

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
                        Log.e("InterstitialAd", "Ad failed to show: ${adError.message}")
                        Timber.tag("InterstitialAd").d("Ad failed to show: ${adError.message}")
                        onNext() // Proceed to the next action
                    }

                    // Optional: Called when the ad is clicked by the user
                    override fun onAdClicked() {
                        // Handle ad click, if needed
                    }

                    // Called when the ad is dismissed by the user (either by clicking 'close' or after the ad ends)
                    override fun onAdDismissedFullScreenContent() {
                        Log.d("InterstitialAd", "Ad dismissed. Reloading the ad.")
                        Timber.tag("InterstitialAd").d("Ad dismissed. Reloading the ad.")
                        interstitialAd = null  // Reset the ad after it is dismissed
                        onNext()  // Trigger the next action after the ad is dismissed
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