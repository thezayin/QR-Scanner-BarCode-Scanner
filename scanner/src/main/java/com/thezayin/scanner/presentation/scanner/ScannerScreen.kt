package com.thezayin.scanner.presentation.scanner

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.thezayin.scanner.presentation.scanner.component.ScannerScreenContent
import com.thezayin.scanner.presentation.scanner.event.ScannerEvent
import org.koin.compose.koinInject

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = koinInject(), onScanSuccess: (List<Pair<String, String>>) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val activity = LocalActivity.current as Activity
    val adManager = viewModel.adManager
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), onResult = { uri: Uri? ->
            uri?.let {
                viewModel.onEvent(ScannerEvent.ImageSelected(uri.toString()), onScanSuccess)
            }
        })

    LaunchedEffect(Unit) {
        adManager.loadAd(activity)
    }

    val batchImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(), onResult = { uris: List<Uri>? ->
            uris?.let { selectedUris ->
                adManager.showAd(
                    activity = activity,
                    showAd = viewModel.remoteConfig.adConfigs.adOnBatchSelection,
                    onNext = {
                        viewModel.onEvent(
                            ScannerEvent.ImagesSelected(selectedUris.map { it.toString() }),
                            onScanSuccess
                        )
                    },
                )
            }
        })

    ScannerScreenContent(
        state = state,
        onBatchClick = { batchImagePickerLauncher.launch(arrayOf("image/*")) },
        onGalleryClick = { imagePickerLauncher.launch("image/*") },
        onFlashToggle = { viewModel.onEvent(ScannerEvent.ToggleFlashlight, onScanSuccess) },
        onZoomChange = { newValue -> viewModel.updateZoom(newValue) },
        onZoomIn = { viewModel.updateZoom(state.zoomLevel + 0.1f) },
        onZoomOut = { viewModel.updateZoom(state.zoomLevel - 0.1f) },
        onCameraReady = { camera -> viewModel.setCamera(camera) },
        onScanSuccess = onScanSuccess,
        viewModel = viewModel
    )
}