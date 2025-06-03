package com.thezayin.framework.remote

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.json.Json
import timber.log.Timber


private const val AD_CONFIGS = "ad_configs"
private const val AD_UNITS = "ad_units"
private const val PURCHASE_IDS = "purchase_ids"

class RemoteConfig(
    private val json: Json
) {
    private val default: Map<String, Any> = mapOf(
        AD_CONFIGS to defaultAdConfigs,
        AD_UNITS to defaultAdUnits,
        PURCHASE_IDS to defaultPurchaseIds
    )

    @SuppressLint("LogNotTimber")
    private val config = FirebaseRemoteConfig.getInstance().apply {
        setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        })
        setDefaultsAsync(default)
        fetchAndActivate().addOnCompleteListener {
            Log.d("RemoteConfig", "fetchAndActivate: ${all.mapValues { (_, v) -> v.asString() }}")
        }
    }

    val adUnits: AdUnits
        get() = try {
            val adUnitsJson = config.getString(AD_UNITS)
            json.decodeFromString(AdUnits.serializer(), adUnitsJson)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            json.decodeFromString(AdUnits.serializer(), defaultAdUnits)
        }

    @OptIn(ExperimentalSerializationApi::class)
    val adConfigs: AdConfigs
        get() = try {
            val adConfigsJson = config.getString(AD_CONFIGS)
            Timber.tag("RemoteConfig").d("AdConfigs JSON: %s", adConfigsJson)
            if (adConfigsJson.isBlank()) {
                Timber.tag("RemoteConfig").e("Received empty or null JSON for AdConfigs")
                AdConfigs()
            } else {
                json.decodeFromString(AdConfigs.serializer(), adConfigsJson)
            }
        } catch (e: MissingFieldException) {
            Timber.tag("RemoteConfig").e(e, "Missing fields in AdConfigs JSON")
            AdConfigs()
        } catch (e: Exception) {
            Timber.tag("RemoteConfig").e(e, "Error decoding AdConfigs JSON")
            AdConfigs()
        }

    @OptIn(ExperimentalSerializationApi::class)
    val purchaseIds: PurchaseIds
        get() = try {
            val purchaseIdsJson = config.getString(PURCHASE_IDS)
            Timber.tag("RemoteConfig").d("PurchaseIds JSON: %s", purchaseIdsJson)
            if (purchaseIdsJson.isBlank()) {
                Timber.tag("RemoteConfig").e("Received empty JSON for PurchaseIds")
                json.decodeFromString(PurchaseIds.serializer(), defaultPurchaseIds)
            } else {
                json.decodeFromString(PurchaseIds.serializer(), purchaseIdsJson)
            }
        } catch (e: Exception) {
            Timber.tag("RemoteConfig").e(e, "Error decoding PurchaseIds JSON")
            json.decodeFromString(PurchaseIds.serializer(), defaultPurchaseIds)
        }
}