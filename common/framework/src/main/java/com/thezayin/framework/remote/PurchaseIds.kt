package com.thezayin.framework.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PurchaseIds(
    @SerialName("monthly") val monthly: String = "android.test.purchased",
    @SerialName("weekly") val weekly: String = "android.test.purchased",
    @SerialName("yearly") val yearly: String = "android.test.purchased",
    @SerialName("lifetime") val lifetime: String = "android.test.purchased"
)

val defaultPurchaseIds = """
        {
          "monthly": "android.test.purchased",
          "weekly": "android.test.purchased",
          "yearly": "android.test.purchased",
          "lifetime": "android.test.purchased"
        }
    """.trimIndent()