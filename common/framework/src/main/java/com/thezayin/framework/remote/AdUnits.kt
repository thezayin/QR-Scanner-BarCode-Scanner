package com.thezayin.framework.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdUnits(
    @SerialName("appOpenAd") val appOpenAd: String = "",
    @SerialName("interstitialAd") val interstitialAd: String = "",
    @SerialName("rewardedAd") val rewardedAd: String = "",
    @SerialName("nativeAd") val nativeAd: String = "",
    @SerialName("bannerAd") val bannerAd: String = ""
)

val defaultAdUnits = """
{
  "appOpenAd": "ca-app-pub-3940256099942544/9257395921",
  "interstitialAd": "ca-app-pub-3940256099942544/1033173712",
  "rewardedAd": "ca-app-pub-3940256099942544/5224354917",
  "nativeAd": "ca-app-pub-3940256099942544/2247696110",
  "bannerAd": "ca-app-pub-3940256099942544/6300978111"
}
""".trimIndent()