package com.thezayin.framework.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdUnits(
    @SerialName("appOpenAd") val appOpenAd: String = "ca-app-pub-1251134228868472/3535468012",
    @SerialName("interstitialAd") val interstitialAd: String = "ca-app-pub-1251134228868472/5179722171",
    @SerialName("rewardedAd") val rewardedAd: String = "ca-app-pub-1251134228868472/4848549688",
    @SerialName("nativeAd") val nativeAd: String = "ca-app-pub-1251134228868472/9371901017",
    @SerialName("bannerAd") val bannerAd: String = "ca-app-pub-1251134228868472/1473537183"
)

val defaultAdUnits = """
{
  "appOpenAd": "ca-app-pub-1251134228868472/3535468012",
  "interstitialAd": "ca-app-pub-1251134228868472/5179722171",
  "rewardedAd": "ca-app-pub-1251134228868472/4848549688",
  "nativeAd": "ca-app-pub-1251134228868472/9371901017",
  "bannerAd": "ca-app-pub-1251134228868472/1473537183"
}
""".trimIndent()