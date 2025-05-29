package com.thezayin.framework.ads.admob.domain.repository

import android.app.Activity

interface InterstitialAdManager {
    fun loadAd(
        activity: Activity
    )

    fun showAd(
        activity: Activity,
        showAd: Boolean,
        adImpression: () -> Unit = {},
        onNext: () -> Unit
    )
}
