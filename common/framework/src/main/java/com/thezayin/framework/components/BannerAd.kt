package com.thezayin.framework.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.thezayin.framework.preferences.PreferencesManager

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BannerAd(
    adId: String = "ca-app-pub-2913057115284606/1324373691",
    showAd: Boolean = true,
    preferencesManager: PreferencesManager
) {
    if (!showAd) return
    if (preferencesManager.isPremiumFlow.value) return
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = adId
                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    )
}