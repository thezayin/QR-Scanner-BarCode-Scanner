package com.thezayin.scanner.presentation.scanner

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.Camera
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.mlkit.vision.common.InputImage
import com.thezayin.framework.ads.admob.domain.repository.InterstitialAdManager
import com.thezayin.framework.preferences.PreferencesManager
import com.thezayin.framework.remote.RemoteConfig
import com.thezayin.framework.session.ScanSessionManager
import com.thezayin.scanner.domain.model.Result
import com.thezayin.scanner.domain.usecase.ScanQrUseCase
import com.thezayin.scanner.presentation.scanner.event.ScannerEvent
import com.thezayin.scanner.presentation.scanner.state.ScannerState
import com.thezayin.values.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("DEPRECATION")
@RequiresApi(Build.VERSION_CODES.O)
class ScannerViewModel(
    private val application: Application,
    private val scanQrUseCase: ScanQrUseCase,
    private val preferencesManager: PreferencesManager,
    private val sessionManager: ScanSessionManager,
    val remoteConfig: RemoteConfig,
    val adManager: InterstitialAdManager
) : ViewModel() {

    private val _state = MutableStateFlow(ScannerState())
    val state: StateFlow<ScannerState> = _state

    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    var primaryColor = preferencesManager.getPrimaryColor()

    init {
        sessionManager.clearScanResults()
    }

    fun setCamera(camera: Camera) {
        this.camera = camera
    }

    fun setImageCapture(capture: ImageCapture) {
        this.imageCapture = capture
    }

    fun onEvent(event: ScannerEvent, onScanSuccess: (List<Pair<String, String>>) -> Unit) {
        when (event) {
            is ScannerEvent.ToggleFlashlight -> toggleFlashlight()
            is ScannerEvent.ChangeZoom -> changeZoom(event.zoomLevel)
            is ScannerEvent.ProcessScanResult -> processScanResult(event.result, onScanSuccess)
            is ScannerEvent.ShowError -> showError(event.message)
            is ScannerEvent.ImagesSelected -> handleBatchImages(event.imageUris, onScanSuccess)
            is ScannerEvent.ImageSelected -> handleImageSelected(event.imageUri, onScanSuccess)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun processScanResult(
        result: String, onScanSuccess: (List<Pair<String, String>>) -> Unit
    ) {
        viewModelScope.launch {
            triggerHapticFeedback(application)
            try {
                val filePath = captureImageAndGetInternalPath()
                sessionManager.saveScanResult(filePath, result)
                onScanSuccess(listOf(filePath to result))
            } catch (e: Exception) {
                showError("Image capture failed: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun captureImageAndGetInternalPath(): String {
        val capture =
            imageCapture ?: throw IllegalStateException("ImageCapture use case is not initialized.")
        return suspendCancellableCoroutine { cont ->
            val internalImagesDir = File(application.filesDir, "scanned_images")
            if (!internalImagesDir.exists()) {
                internalImagesDir.mkdirs()
            }
            val photoFile = File(
                internalImagesDir, "scanned_image_${System.currentTimeMillis()}.jpg"
            )

            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            capture.takePicture(outputOptions,
                ContextCompat.getMainExecutor(application),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        cont.resume(photoFile.absolutePath)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        cont.resumeWithException(exception)
                    }
                })
        }
    }

    private fun handleImageSelected(
        imageUri: String, onScanSuccess: (List<Pair<String, String>>) -> Unit
    ) {
        viewModelScope.launch {
            val image = InputImage.fromFilePath(application, Uri.parse(imageUri))
            when (val result = scanQrUseCase.execute(image)) {
                is Result.Success -> {
                    triggerHapticFeedback(application)
                    sessionManager.saveScanResult(imageUri, result.data.content)
                    val scanResult = listOf(imageUri to result.data.content)
                    onScanSuccess(scanResult)
                }

                is Result.Failure -> {
                    Toast.makeText(
                        application,
                        application.getString(R.string.qr_code_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun handleBatchImages(
        imageUris: List<String>, onScanSuccess: (List<Pair<String, String>>) -> Unit
    ) {
        viewModelScope.launch {
            val scannedResults = mutableListOf<Pair<String, String>>()
            for (imageUri in imageUris) {
                val image = InputImage.fromFilePath(application, Uri.parse(imageUri))
                val result = scanQrUseCase.execute(image)
                if (result is Result.Success) {
                    sessionManager.saveScanResult(imageUri, result.data.content)
                    scannedResults.add(imageUri to result.data.content)
                }
            }
            if (scannedResults.isNotEmpty()) {
                triggerHapticFeedback(application)
                onScanSuccess(scannedResults)
            } else {
                Toast.makeText(
                    application,
                    application.getString(R.string.qr_code_not_found),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun toggleFlashlight() {
        viewModelScope.launch {
            val currentFlashState = _state.value.isFlashlightOn
            try {
                camera?.cameraControl?.enableTorch(!currentFlashState)
                _state.value = _state.value.copy(isFlashlightOn = !currentFlashState)
            } catch (e: Exception) {
                _state.value =
                    _state.value.copy(error = application.getString(R.string.flashlight_error))
            }
        }
    }

    private fun changeZoom(zoomLevel: Float) {
        viewModelScope.launch {
            try {
                camera?.cameraControl?.setZoomRatio(zoomLevel)
                _state.value = _state.value.copy(zoomLevel = zoomLevel)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun triggerHapticFeedback(context: Context) {
        if (preferencesManager.getVibrateEnabled()) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        if (preferencesManager.getBeepEnabled()) {
            val mediaPlayer = MediaPlayer.create(context, R.raw.scanner_sound)
            mediaPlayer.start()
        }
    }

    fun updateZoom(zoomLevel: Float) {
        viewModelScope.launch {
            try {
                camera?.cameraControl?.setZoomRatio(zoomLevel)
                _state.value = _state.value.copy(zoomLevel = zoomLevel)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    private fun showError(message: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = message)
        }
    }
}