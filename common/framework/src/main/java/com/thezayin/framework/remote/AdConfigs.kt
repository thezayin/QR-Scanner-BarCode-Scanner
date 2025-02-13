package com.thezayin.framework.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdConfigs(
    @SerialName("adOnResume") val adOnResume: Boolean = false,
    @SerialName("switchAdResume") val switchAdResume: Boolean = false,
    @SerialName("adOnSplash") val adOnSplash: Boolean = false,
    @SerialName("switchAdOnSplash") val switchAdOnSplash: Boolean = false,
    @SerialName("bottomAdAtOnboarding") val bottomAdAtOnboarding: Boolean = false,
    @SerialName("switchBottomAdAtOnboarding") val switchBottomAdAtOnboarding: Boolean = false,
    @SerialName("adAtOnboardingDone") val adAtOnboardingDone: Boolean = false,
    @SerialName("switchAdAtOnboardingDone") val switchAdAtOnboardingDone: Boolean = false,
    @SerialName("adOnBottomHome") val adOnBottomHome: Boolean = false,
    @SerialName("adOnBatchSelection") val adOnBatchSelection: Boolean = false,
    @SerialName("bottomAdOnScanResult") val bottomAdOnScanResult: Boolean = false,
    @SerialName("adOnScanResultBackClick") val adOnScanResultBackClick: Boolean = false,
    @SerialName("adOnScanOpen") val adOnScanOpen: Boolean = false,
    @SerialName("adOnHistoryCardClick") val adOnHistoryCardClick: Boolean = false,
    @SerialName("adOnHistoryShareClick") val adOnHistoryShareClick: Boolean = false,
    @SerialName("adOnHistoryDownloadClick") val adOnHistoryDownloadClick: Boolean = false,
    @SerialName("adOnCreateOption") val adOnCreateOption: Boolean = false,
    @SerialName("adOnTopRowOption") val adOnTopRowOption: Boolean = false,
    @SerialName("adOnGenerateQr") val adOnGenerateQr: Boolean = false,
    @SerialName("adOnBottomMenu") val adOnBottomMenu: Boolean = false,
    @SerialName("adOnExist") val adOnExist: Boolean = false
)

val defaultAdConfigs = """
   {
   "adOnResume": false,
   "adOnExist": false,
   "switchAdResume": false,
   "adOnSplash":false,
   "adOnBottomMenu":false,
   "switchAdOnSplash":false,
   "bottomAdAtOnboarding":false,
   "switchBottomAdAtOnboarding":false,
   "adAtOnboardingDone":false,
   "switchAdAtOnboardingDone":false,
   "adOnBottomHome":false,
   "adOnBatchSelection":false,
   "bottomAdOnScanResult":false,
   "adOnScanResultBackClick":false,
   "adOnScanOpen":false,
   "adOnHistoryCardClick":false,
   "adOnHistoryShareClick":false,
   "adOnHistoryDownloadClick":false,
   "adOnCreateOption":false,
   "adOnTopRowOption":false,
   "adOnGenerateQr":false
}
""".trimIndent()