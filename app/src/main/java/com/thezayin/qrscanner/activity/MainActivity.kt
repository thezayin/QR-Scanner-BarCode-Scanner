package com.thezayin.qrscanner.activity

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.thezayin.framework.ads.functions.appOpenAd
import com.thezayin.framework.ads.functions.interstitialAd
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.qrscanner.navigation.RootNavGraph
import com.thezayin.qrscanner.ui.theme.QRScannerTheme
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val preferencesManager: PreferencesManager by inject()
    private val remoteConfig: RemoteConfig by inject()
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                initializeScreen()
            } else {
                showPermissionDeniedDialog()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val savedLanguage = preferencesManager.selectedLanguageFlow.value ?: "en"
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(savedLanguage))
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .build()
        )
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initializeScreen()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun initializeScreen() {
        enableEdgeToEdge()
        setContent {
            val primaryColor by preferencesManager.primaryColorFlow.collectAsState()
            val darkTheme by preferencesManager.darkThemeFlow.collectAsState()

            QRScannerTheme(
                darkTheme = darkTheme,
                userSelectedPrimary = primaryColor
            ) {
                Surface {
                    RootNavGraph(primaryColor = primaryColor, remoteConfig = remoteConfig)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (remoteConfig.adConfigs.switchAdResume) {
            this.appOpenAd(
                showAd = remoteConfig.adConfigs.adOnResume,
                adUnitId = remoteConfig.adUnits.appOpenAd,
                showLoading = {},
                hideLoading = { },
                callback = {},
            )
        } else {
            this.interstitialAd(
                showAd = remoteConfig.adConfigs.adOnResume,
                adUnitId = remoteConfig.adUnits.interstitialAd,
                showLoading = {},
                hideLoading = { },
                callback = {},
            )
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Camera Permission Required")
            .setMessage("Camera permission is required to scan QR codes. Please grant the permission.")
            .setPositiveButton("OK") { _, _ ->
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}