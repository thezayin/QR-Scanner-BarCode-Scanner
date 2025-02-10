@file:Suppress("DEPRECATION")

package com.thezayin.scanner.presentation.scanner.component

import android.os.Build
import android.util.Size
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.thezayin.scanner.presentation.scanner.ScannerViewModel
import com.thezayin.scanner.presentation.scanner.analyzer.BarcodeAnalyzer
import com.thezayin.scanner.presentation.scanner.event.ScannerEvent

/**
 * A composable function that integrates CameraX preview and QR code scanner.
 *
 * This component sets up the camera, initializes the scanner, and processes
 * images using ML Kit's barcode scanner.
 *
 * @param modifier Modifier for layout adjustments.
 * @param onCameraReady Callback invoked when the camera is initialized.
 * @param viewModel ViewModel responsible for handling scan events.
 * @param onScanSuccess Callback for handling successful scan results (Navigates to next screen).
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onCameraReady: (Camera) -> Unit,
    viewModel: ScannerViewModel,
    onScanSuccess: (List<Pair<String, String>>) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var imageAnalysis by remember { mutableStateOf<ImageAnalysis?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = modifier.fillMaxSize(),
        update = { previewView ->
            cameraProviderFuture.addListener({
                cameraProvider = cameraProviderFuture.get()
                cameraProvider?.unbindAll()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                imageCapture?.let { viewModel.setImageCapture(it) }
                val barcodeAnalyzer = BarcodeAnalyzer(
                    onQrCodeScanned = { scannedText ->
                        viewModel.onEvent(
                            ScannerEvent.ProcessScanResult(scannedText),
                            onScanSuccess = onScanSuccess
                        )
                    },
                    onStopScanning = {
                        imageAnalysis?.clearAnalyzer()
                        cameraProvider?.unbind(imageAnalysis)
                    }
                )
                imageAnalysis?.setAnalyzer(ContextCompat.getMainExecutor(context), barcodeAnalyzer)
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    val camera: Camera = cameraProvider!!.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis,
                        imageCapture
                    )
                    onCameraReady(camera)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}