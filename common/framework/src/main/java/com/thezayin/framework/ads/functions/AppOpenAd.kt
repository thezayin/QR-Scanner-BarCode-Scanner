package com.thezayin.framework.ads.functions

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.thezayin.framework.ads.loader.GoogleAppOpenAdLoader

fun Activity.appOpenAd(
    showAd: Boolean,
    adUnitId: String,
    showLoading: () -> Unit,
    hideLoading: () -> Unit,
    callback: () -> Unit
) = runCatching {
    if (!showAd) return@runCatching callback()
    showLoading()
    GoogleAppOpenAdLoader.loadAd(
        context = this@appOpenAd,
        adUnitId = adUnitId,
        onAdLoading = showLoading,
        onAdLoaded = { appOpenAd ->
            hideLoading()
            appOpenAd.fullScreenContentCallback = AdmobAppOpenListener(callback)
            appOpenAd.show(this@appOpenAd)
        },
        onAdFailed = {
            hideLoading()
            callback()
        }
    )
}.onFailure {
    hideLoading()
    callback()
}


internal class AdmobAppOpenListener(
    private val callback: () -> Unit,
) : FullScreenContentCallback() {
    private var clicks = 0

    override fun onAdClicked() {
        super.onAdClicked()
        clicks++
    }

    override fun onAdDismissedFullScreenContent() {
        super.onAdDismissedFullScreenContent()
        callback.invoke()
    }

    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
        super.onAdFailedToShowFullScreenContent(adError)
        callback.invoke()
    }
}