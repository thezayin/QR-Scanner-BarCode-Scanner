package com.thezayin.scanner.presentation.result

import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAd
import com.thezayin.framework.ads.admob.domain.repository.InterstitialAdManager
import com.thezayin.framework.ads.loader.GoogleNativeAdLoader
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.framework.session.ScanSessionManager
import com.thezayin.scanner.domain.model.Result
import com.thezayin.scanner.domain.model.ResultScreenItem
import com.thezayin.scanner.domain.usecase.AddProductToDbUseCase
import com.thezayin.scanner.domain.usecase.FetchProductDetailsUseCase
import com.thezayin.scanner.domain.usecase.UpdateFavoriteUseCase
import com.thezayin.scanner.presentation.result.event.ResultScreenEvent
import com.thezayin.scanner.presentation.result.state.ResultScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class ResultScreenViewModel(
    application: Application,
    private val sessionManager: ScanSessionManager,
    private val fetchProductDetailsUseCase: FetchProductDetailsUseCase,
    private val addProductToDbUseCase: AddProductToDbUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    val remoteConfig: RemoteConfig,
    val adManager: InterstitialAdManager,
    val preferencesManager: PreferencesManager
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(ResultScreenState())
    val state: StateFlow<ResultScreenState> = _state

    var nativeAd = mutableStateOf<NativeAd?>(null)
        private set

    init {
        loadResults()
    }

    fun initManager(activity: Activity) {
        adManager.loadAd(activity)
    }

    fun getNativeAd(context: Context) = viewModelScope.launch {
        if (remoteConfig.adConfigs.bottomAdOnScanResult) {
            GoogleNativeAdLoader.loadNativeAd(
                preferencesManager = preferencesManager,
                context = context,
                adUnitId = remoteConfig.adUnits.nativeAd,
                onNativeAdLoaded = {
                    nativeAd.value = it
                })
        }
    }

    private fun loadResults() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val sessionList = sessionManager.getScanResults()
            val items = sessionList.map { (imageUri, resultText) ->
                val type = classifyScan(resultText)
                ResultScreenItem(
                    imageUri = imageUri,
                    result = resultText,
                    type = type,
                    timestamp = System.currentTimeMillis(),
                    isFavorite = false
                )
            }.toMutableList()

            items.forEachIndexed { index, rawItem ->
                if (rawItem.type == "Text" || rawItem.type == "Barcode") {
                    val productResult = fetchProductDetailsUseCase.execute(rawItem.result)
                    if (productResult is Result.Success) {
                        val itemWithDetails = rawItem.copy(
                            name = productResult.data.name,
                            imageUrl = productResult.data.imageUrl,
                            brands = productResult.data.brands,
                            links = productResult.data.links,
                            productFound = true
                        )
                        val addResult = addProductToDbUseCase.execute(itemWithDetails)
                        if (addResult is Result.Success) {
                            items[index] = addResult.data
                        } else {
                            items[index] = itemWithDetails
                        }
                    } else {
                        val notFoundItem = rawItem.copy(productFound = false)
                        val addResult = addProductToDbUseCase.execute(notFoundItem)
                        if (addResult is Result.Success) {
                            items[index] = addResult.data
                        } else {
                            items[index] = notFoundItem
                        }
                    }
                } else {
                    val updatedItem = rawItem.copy(productFound = null)
                    val addResult = addProductToDbUseCase.execute(updatedItem)
                    if (addResult is Result.Success) {
                        items[index] = addResult.data
                    } else {
                        items[index] = updatedItem
                    }
                }
            }

            _state.value = ResultScreenState(scanItems = items, isLoading = false)
        }
    }

    private fun classifyScan(resultText: String): String {
        val lower = resultText.lowercase(Locale.getDefault())
        return when {
            lower.startsWith("http://") || lower.startsWith("https://") -> "URL"
            lower.startsWith("wifi:") -> "WIFI"
            lower.startsWith("begin:vcard") -> "VCARD"
            lower.startsWith("sms:") -> "SMS"
            lower.startsWith("geo:") -> "GEO"
            lower.startsWith("mailto:") -> "EMAIL"
            lower.startsWith("tel:") -> "CALL"
            else -> when {
                resultText.contains("WIFI:S:") -> "WIFI"
                resultText.contains("BEGIN:VEVENT") -> "CALENDAR"
                resultText.contains("BEGIN:VCARD") -> "CONTACT"
                else -> "Barcode"
            }
        }
    }


    fun onEvent(event: ResultScreenEvent) {
        when (event) {
            is ResultScreenEvent.Refresh -> loadResults()
            is ResultScreenEvent.OpenItem -> openItem(event.item, event.context)
            is ResultScreenEvent.ShareItem -> shareItem(event.item)
            is ResultScreenEvent.CopyItem -> copyItem(event.item)
            is ResultScreenEvent.ConnectWifi -> connectWifi(event.item)
            is ResultScreenEvent.CopyPassword -> copyPassword(event.password)
            is ResultScreenEvent.ShareWifi -> shareWifi(event.ssid, event.password)
            is ResultScreenEvent.CopyWifi -> copyWifi(event.ssid, event.password)
            is ResultScreenEvent.ToggleFavorite -> toggleFavorite(event.item)
        }
    }

    private fun openItem(item: ResultScreenItem, context: Context) {
        when {
            item.result.startsWith("tel:") -> {
                val intent = Intent(Intent.ACTION_DIAL, item.result.toUri())
                context.startActivity(intent)
            }

            item.result.startsWith("sms:") -> {
                val intent = Intent(Intent.ACTION_VIEW, item.result.toUri())
                context.startActivity(intent)
            }

            item.result.startsWith("mailto:") -> {
                val intent = Intent(Intent.ACTION_SENDTO, item.result.toUri())
                context.startActivity(intent)
            }

            item.result.startsWith("http://") || item.result.startsWith("https://") -> {
                val intent = Intent(Intent.ACTION_VIEW, item.result.toUri())
                context.startActivity(intent)
            }

            item.result.startsWith("wifi:") -> {
                val wifiDetails = item.result.removePrefix("wifi:")
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = "wifi:$wifiDetails".toUri()
                }
                context.startActivity(intent)
            }

            else -> {
                Toast.makeText(context, "Unsupported QR type: ${item.result}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun shareItem(item: ResultScreenItem) {
        val context = getApplication<Application>()
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, item.result)
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Share via").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(chooserIntent)
    }

    private fun copyItem(item: ResultScreenItem) {
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Scanned Result", item.result)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun connectWifi(item: ResultScreenItem) {
        val context = getApplication<Application>()
        Toast.makeText(context, "Connecting to WiFi: ${item.result}", Toast.LENGTH_SHORT).show()
    }

    private fun copyPassword(password: String) {
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("WiFi Password", password)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "WiFi password copied", Toast.LENGTH_SHORT).show()
    }

    private fun shareWifi(ssid: String, password: String) {
        val context = getApplication<Application>()
        val text = "WiFi Network: $ssid\nPassword: $password"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Share via").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(chooserIntent)
    }

    private fun copyWifi(ssid: String, password: String) {
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = "WiFi Network: $ssid\nPassword: $password"
        val clip = ClipData.newPlainText("WiFi Network", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "WiFi network details copied", Toast.LENGTH_SHORT).show()
    }


    private fun toggleFavorite(item: ResultScreenItem) {
        viewModelScope.launch {
            val updatedItem = item.copy(isFavorite = !item.isFavorite)
            val result = updateFavoriteUseCase.execute(updatedItem)
            if (result is Result.Success) {
                _state.value = _state.value.copy(
                    scanItems = _state.value.scanItems.map {
                        if (it.id == item.id) {
                            updatedItem
                        } else it
                    })
            } else {
                Toast.makeText(getApplication(), "Error updating favorite", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}