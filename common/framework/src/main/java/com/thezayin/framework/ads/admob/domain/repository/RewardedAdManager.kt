package com.thezayin.framework.ads.admob.domain.repository

import android.app.Activity
import com.google.android.gms.ads.rewarded.RewardItem

interface RewardedAdManager {
    fun loadAd(activity: Activity)
    fun showAd(
        activity: Activity,
        showAd: Boolean,
        adImpression: () -> Unit = {},
        onReward: (RewardItem) -> Unit = {},
        onNext: () -> Unit
    )
}
