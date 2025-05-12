package com.thezayin.framework.ads.admob.data.repository

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.thezayin.framework.ads.admob.domain.repository.RewardedAdManager
import com.thezayin.framework.remote.RemoteConfig
import timber.log.Timber

/**
 * Implementation of the RewardedAdManager interface that handles loading and displaying
 * rewarded ads using Google's AdMob SDK.
 */
class RewardedAdManagerImpl(
    // Remote configuration to fetch ad unit IDs
    private val remoteConfig: RemoteConfig,
) : RewardedAdManager {

    // Rewarded ad instance
    private var rewardedAd: RewardedAd? = null

    // Ad Unit ID (replace with your actual AdMob Rewarded Ad Unit ID)
    private var adId: String = remoteConfig.adUnits.rewardedAd

    /**
     * Load the Rewarded Ad. This method will load the ad only if it is not already loaded.
     * Ad will be loaded asynchronously.
     */
    override fun loadAd(activity: Activity) {
        Timber.tag("RewardedAd").d("Loading rewarded ad")

        // Load the ad only if it's not already loaded
        if (rewardedAd == null) {
            // Create an ad request and load the rewarded ad
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                activity, // The activity in which the ad will be shown
                adId,     // The ad unit ID
                adRequest, // The ad request
                object : RewardedAdLoadCallback() { // Callback to handle the loading result
                    override fun onAdLoaded(ad: RewardedAd) {
                        Timber.tag("RewardedAd").d("Rewarded Ad loaded successfully")
                        rewardedAd = ad // Save the loaded ad
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Timber.tag("RewardedAd")
                            .d("Rewarded Ad failed to load: ${loadAdError.message}")
                        rewardedAd = null // Reset the ad if loading fails
                    }
                }
            )
        }
    }

    /**
     * Show the Rewarded Ad. If the ad is loaded, it will be displayed.
     * Once the ad is dismissed, the next action will be triggered.
     */
    override fun showAd(
        activity: Activity, // The activity in which the ad will be shown
        showAd: Boolean,    // Whether or not to show the ad
        adImpression: () -> Unit,  // Callback to handle ad impression
        onReward: (RewardItem) -> Unit, // Callback to handle the reward
        onNext: () -> Unit    // Callback to handle the next action after the ad is dismissed
    ) {
        // Check if we need to show the ad
        if (showAd) {
            rewardedAd?.let { ad ->  // If the ad is loaded, show it
                ad.show(activity) { rewardItem: RewardItem ->
                    onReward(rewardItem)  // The user gets a reward
                }

                // FullScreenContentCallback to handle the ad's lifecycle
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    // Called when the ad impression is logged
                    override fun onAdImpression() {
                        super.onAdImpression()
                        adImpression()  // Trigger ad impression callback
                    }

                    // Called when the ad is shown successfully
                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                    }

                    // Called if the ad fails to show
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Timber.tag("RewardedAd").d("Rewarded Ad failed to show: ${adError.message}")
                        onNext() // Proceed with the next action
                    }

                    // Optional: Called when the ad is clicked
                    override fun onAdClicked() {
                        // Handle ad click if needed
                    }

                    // Called when the ad is dismissed
                    override fun onAdDismissedFullScreenContent() {
                        Timber.tag("RewardedAd").d("Rewarded Ad dismissed. Reloading the ad.")
                        rewardedAd = null  // Reset the ad after dismissal
                        onNext()  // Proceed to the next action after ad dismissal
                    }
                }
            } ?: run {
                onNext()  // If the ad isn't loaded, proceed with the next action
            }
        } else {
            // If the showAd flag is false, skip the ad and proceed to the next action
            onNext()
        }
    }
}