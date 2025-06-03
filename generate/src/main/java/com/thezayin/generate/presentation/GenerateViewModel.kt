package com.thezayin.generate.presentation

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thezayin.framework.ads.admob.domain.repository.InterstitialAdManager
import com.thezayin.framework.ads.admob.domain.repository.RewardedAdManager
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.generate.domain.model.QrContent
import com.thezayin.generate.domain.model.QrType
import com.thezayin.generate.domain.usecase.GenerateQrUseCase
import com.thezayin.generate.presentation.event.GenerateEvent
import com.thezayin.generate.presentation.state.GenerateState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class GenerateViewModel(
    application: Application,
    private val generateQrUseCase: GenerateQrUseCase,
    val remoteConfig: RemoteConfig,
    val pref: PreferencesManager,
    val adManager: InterstitialAdManager,
    val rewardedAdManager: RewardedAdManager,
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(GenerateState())
    val state: StateFlow<GenerateState> = _state.asStateFlow()

    fun onEvent(event: GenerateEvent) {
        when (event) {
            is GenerateEvent.SelectType -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedType = event.type,
                        showMainListOnly = false,
                        generatedQrBitmap = null,
                        isGenerating = false
                    )
                }
            }

            GenerateEvent.GenerateQrCode -> {
                if (_state.value.isGenerating) {
                    Timber.tag("GenerateViewModel")
                        .d("GenerateQrCode event IGNORED (already generating).")
                    return
                }

                _state.update { it.copy(isGenerating = true, error = null) }
                Timber.tag("GenerateViewModel")
                    .d("GenerateQrCode event PROCESSING. isGenerating = true.")
                viewModelScope.launch {
                    try {
                        val content = buildQrContentFromState(_state.value)
                        Timber.tag("GenerateViewModel").d("Coroutine: Generating QR for: $content")
                        val bmp = generateQrUseCase(content)
                        _state.update {
                            it.copy(generatedQrBitmap = bmp, isGenerating = false)
                        }
                        Timber.tag("GenerateViewModel")
                            .d("Coroutine: Generation SUCCESS. isGenerating = false")
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Timber.tag("GenerateViewModel")
                            .e(e, "Coroutine: Error generating QR: ${e.message}")
                        _state.update {
                            it.copy(error = e.message, isGenerating = false)
                        }
                        Timber.tag("GenerateViewModel")
                            .d("Coroutine: Generation FAILED. isGenerating = false")
                    }
                }
            }

            GenerateEvent.BackPressed -> {
                _state.update { currentState ->
                    if (!currentState.showMainListOnly) {
                        currentState.copy(
                            showMainListOnly = true,
                            generatedQrBitmap = null,
                            isGenerating = false
                        )
                    } else {
                        currentState
                    }
                }
            }

            is GenerateEvent.UpdateCallNumber -> _state.update { it.copy(callNumber = event.number) }
            is GenerateEvent.UpdateSmsNumber -> _state.update { it.copy(smsNumber = event.number) }
            is GenerateEvent.UpdateSmsMessage -> _state.update { it.copy(smsMessage = event.message) }
            is GenerateEvent.UpdateEmailAddress -> _state.update { it.copy(emailAddress = event.address) }
            is GenerateEvent.UpdateEmailSubject -> _state.update { it.copy(emailSubject = event.subject) }
            is GenerateEvent.UpdateEmailBody -> _state.update { it.copy(emailBody = event.body) }
            is GenerateEvent.UpdateWebsiteUrl -> _state.update { it.copy(websiteUrl = event.url) }
            is GenerateEvent.UpdateText -> _state.update { it.copy(text = event.text) }
            is GenerateEvent.UpdateClipboard -> _state.update { it.copy(clip = event.clip) }
            is GenerateEvent.UpdateWifiSsid -> _state.update { it.copy(wifiSsid = event.ssid) }
            is GenerateEvent.UpdateWifiPassword -> _state.update { it.copy(wifiPassword = event.password) }
            is GenerateEvent.UpdateWifiEncryption -> _state.update { it.copy(wifiEncryption = event.encryption) }
            is GenerateEvent.UpdateCalendarTitle -> _state.update { it.copy(calendarTitle = event.title) }
            is GenerateEvent.UpdateCalendarDescription -> _state.update {
                it.copy(
                    calendarDescription = event.description
                )
            }

            is GenerateEvent.UpdateCalendarStart -> _state.update { it.copy(calendarStart = event.start) }
            is GenerateEvent.UpdateCalendarEnd -> _state.update { it.copy(calendarEnd = event.end) }
            is GenerateEvent.UpdateContactName -> _state.update { it.copy(contactName = event.name) }
            is GenerateEvent.UpdateContactPhone -> _state.update { it.copy(contactPhone = event.phone) }
            is GenerateEvent.UpdateContactEmail -> _state.update { it.copy(contactEmail = event.email) }
            is GenerateEvent.UpdateLocationLat -> _state.update { it.copy(locationLat = event.lat) }
            is GenerateEvent.UpdateLocationLong -> _state.update { it.copy(locationLong = event.long) }
            is GenerateEvent.UpdateCode39 -> _state.update { it.copy(code39Code = event.code) }
            is GenerateEvent.UpdateCode128 -> _state.update { it.copy(code128Code = event.code) }
            is GenerateEvent.UpdateItf -> _state.update { it.copy(itfCode = event.code) }
            is GenerateEvent.UpdatePdf417 -> _state.update { it.copy(pdf417Code = event.code) }
            is GenerateEvent.UpdateCodabar -> _state.update { it.copy(codabarCode = event.code) }
            is GenerateEvent.UpdateDataMatrix -> _state.update { it.copy(dataMatrixCode = event.code) }
            is GenerateEvent.UpdateAztec -> _state.update { it.copy(aztecCode = event.code) }

            GenerateEvent.DownloadQrCode -> {
                _state.value.generatedQrBitmap?.let { bmp ->
                    downloadQrCode(bmp)
                    _state.update { it.copy(showDownloadSuccess = true) }
                }
            }

            GenerateEvent.ShareQrCode -> {
                _state.value.generatedQrBitmap?.let { bmp ->
                    shareQrCode(bmp)
                }
            }

            GenerateEvent.DismissDownloadSuccess -> {
                _state.update { it.copy(showDownloadSuccess = false) }
            }
        }
    }

    fun initManager(activity: Activity) {
        adManager.loadAd(activity)
        rewardedAdManager.loadAd(activity)
    }

    private fun buildQrContentFromState(currentState: GenerateState): QrContent {
        return when (currentState.selectedType) {
            QrType.CALL -> QrContent.Call(phoneNumber = currentState.callNumber)
            QrType.SMS -> QrContent.Sms(currentState.smsNumber, currentState.smsMessage)
            QrType.EMAIL -> QrContent.Email(
                currentState.emailAddress,
                currentState.emailSubject,
                currentState.emailBody
            )

            QrType.WEBSITE -> QrContent.Website(currentState.websiteUrl)
            QrType.TEXT -> QrContent.Text(currentState.text)
            QrType.CLIPBOARD -> QrContent.Clipboard(currentState.clip)
            QrType.WIFI -> QrContent.Wifi(
                currentState.wifiSsid,
                currentState.wifiPassword,
                currentState.wifiEncryption
            )

            QrType.CALENDAR -> QrContent.Calendar(
                title = currentState.calendarTitle,
                description = currentState.calendarDescription,
                startTime = currentState.calendarStart.toLongOrNull() ?: 0L,
                endTime = currentState.calendarEnd.toLongOrNull() ?: 0L
            )

            QrType.CONTACT -> QrContent.Contact(
                currentState.contactName,
                currentState.contactPhone,
                currentState.contactEmail
            )

            QrType.LOCATION -> QrContent.Location(
                currentState.locationLat,
                currentState.locationLong
            )

            QrType.CODE_39 -> QrContent.Code39(currentState.code39Code)
            QrType.CODE_128 -> QrContent.Code128(currentState.code128Code)
            QrType.ITF -> QrContent.Itf(currentState.itfCode)
            QrType.PDF_417 -> QrContent.Pdf417(currentState.pdf417Code)
            QrType.CODABAR -> QrContent.Codabar(currentState.codabarCode)
            QrType.DATAMATRIX -> QrContent.DataMatrix(currentState.dataMatrixCode)
            QrType.AZTEC -> QrContent.Aztec(currentState.aztecCode)
        }
    }

    private fun downloadQrCode(bitmap: Bitmap) {
        val context = getApplication<Application>()
        val filename = "QR_${System.currentTimeMillis()}.png"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            try {
                resolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                }
                Timber.tag("GenerateViewModel").d("QR Code downloaded successfully to $uri")
            } catch (e: Exception) {
                Timber.tag("GenerateViewModel").e(e, "Error downloading QR Code")
                resolver.delete(uri, null, null)
            }
        } ?: Timber.tag("GenerateViewModel").e("Failed to create MediaStore entry for download.")
    }

    private fun shareQrCode(bitmap: Bitmap) {
        val context = getApplication<Application>()
        val filename =
            "QR_Share_${System.currentTimeMillis()}.png"
        val cachePath =
            File(context.cacheDir, "images_to_share")
        if (!cachePath.exists()) {
            cachePath.mkdirs()
        }
        val file = File(cachePath, filename)
        var fileUri: Uri? = null

        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            fileUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Timber.tag("GenerateViewModel").e(e, "Error creating file for sharing")
            return
        }

        fileUri?.let {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, it)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Share QR Code").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(chooser)
                Timber.tag("GenerateViewModel").d("Share intent started for $it")
            } catch (e: Exception) {
                Timber.tag("GenerateViewModel").e(e, "Error starting share intent")
            }
        } ?: Timber.tag("GenerateViewModel").e("File URI for sharing is null.")
    }
}