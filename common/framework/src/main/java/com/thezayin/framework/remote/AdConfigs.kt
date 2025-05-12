package com.thezayin.framework.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdConfigs(
    @SerialName("adOnResume") val adOnResume: Boolean = true,
    @SerialName("switchAdResume") val switchAdResume: Boolean = true,
    @SerialName("adOnSplash") val adOnSplash: Boolean = true,
    @SerialName("switchAdOnSplash") val switchAdOnSplash: Boolean = true,
    @SerialName("bottomAdAtOnboarding") val bottomAdAtOnboarding: Boolean = true,
    @SerialName("switchBottomAdAtOnboarding") val switchBottomAdAtOnboarding: Boolean = true,
    @SerialName("adAtOnboardingDone") val adAtOnboardingDone: Boolean = true,
    @SerialName("switchAdAtOnboardingDone") val switchAdAtOnboardingDone: Boolean = true,
    @SerialName("adOnBottomHome") val adOnBottomHome: Boolean = true,
    @SerialName("adOnBatchSelection") val adOnBatchSelection: Boolean = true,
    @SerialName("bottomAdOnScanResult") val bottomAdOnScanResult: Boolean = true,
    @SerialName("adOnScanResultBackClick") val adOnScanResultBackClick: Boolean = true,
    @SerialName("adOnScanOpen") val adOnScanOpen: Boolean = true,
    @SerialName("adOnHistoryCardClick") val adOnHistoryCardClick: Boolean = true,
    @SerialName("adOnHistoryShareClick") val adOnHistoryShareClick: Boolean = true,
    @SerialName("adOnHistoryDownloadClick") val adOnHistoryDownloadClick: Boolean = true,
    @SerialName("adOnCreateOption") val adOnCreateOption: Boolean = true,
    @SerialName("adOnTopRowOption") val adOnTopRowOption: Boolean = true,
    @SerialName("adOnGenerateQr") val adOnGenerateQr: Boolean = true,
    @SerialName("adOnBottomMenu") val adOnBottomMenu: Boolean = true,
    @SerialName("adOnExist") val adOnExist: Boolean = true
)

val defaultAdConfigs = """
   {
   "adOnResume": true,
   "adOnExist": true,
   "switchAdResume": true,
   "adOnSplash":true,
   "adOnBottomMenu":true,
   "switchAdOnSplash":true,
   "bottomAdAtOnboarding":true,
   "switchBottomAdAtOnboarding":true,
   "adAtOnboardingDone":true,
   "switchAdAtOnboardingDone":true,
   "adOnBottomHome":true,
   "adOnBatchSelection":true,
   "bottomAdOnScanResult":true,
   "adOnScanResultBackClick":true,
   "adOnScanOpen":true,
   "adOnHistoryCardClick":true,
   "adOnHistoryShareClick":true,
   "adOnHistoryDownloadClick":true,
   "adOnCreateOption":true,
   "adOnTopRowOption":true,
   "adOnGenerateQr":true
}
""".trimIndent()