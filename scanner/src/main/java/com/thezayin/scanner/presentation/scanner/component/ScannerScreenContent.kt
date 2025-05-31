package com.thezayin.scanner.presentation.scanner.component

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.thezayin.framework.components.ScannerOverlay
import com.thezayin.scanner.presentation.scanner.ScannerViewModel
import com.thezayin.scanner.presentation.scanner.event.ScannerEvent
import com.thezayin.scanner.presentation.scanner.state.ScannerState
import ir.kaaveh.sdpcompose.sdp

@Composable
fun ScannerScreenContent(
    state: ScannerState,
    viewModel: ScannerViewModel,
    onHeaderGalleryClick: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            if (errorMessage.isNotBlank()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                viewModel.onEvent(ScannerEvent.ShowError(null))
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 8.sdp),
                contentAlignment = Alignment.TopCenter
            ) {
                if (state.isBatchModeActive) {
                    BatchScanSnackbar(
                        scannedCount = state.batchScannedCodes.size,
                        onCancelBatch = { viewModel.onEvent(ScannerEvent.CancelBatchScan) },
                        onConfirmBatch = { viewModel.onEvent(ScannerEvent.ConfirmBatchScan) }
                    )
                } else {
                    HeaderSection(
                        onBatchClick = {
                            if (state.isCameraReady) {
                                viewModel.onEvent(ScannerEvent.StartBatchScan)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Camera initializing, please wait...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onGalleryClick = onHeaderGalleryClick,
                        onFlashToggle = { viewModel.onEvent(ScannerEvent.ToggleFlashlight) },
                        isFlashOn = state.isFlashlightOn
                    )
                }
            }
        },
        bottomBar = {
            if (!state.isBatchModeActive) {
                ZoomControlsSection(
                    primaryColor = viewModel.primaryColor,
                    zoomLevel = state.zoomLevel,
                    // Pass camera's actual min and max zoom ratios to the UI
                    minZoomRatio = state.minZoomRatio,
                    maxZoomRatio = state.maxZoomRatio,
                    // Use ChangeZoom event for both slider and buttons for consistent behavior
                    onZoomChange = { newZoomLevel ->
                        viewModel.onEvent(
                            ScannerEvent.ChangeZoom(
                                newZoomLevel
                            )
                        )
                    },
                    onZoomIn = { viewModel.onEvent(ScannerEvent.ChangeZoom(state.zoomLevel + 0.5f)) },
                    onZoomOut = { viewModel.onEvent(ScannerEvent.ChangeZoom(state.zoomLevel - 0.5f)) }
                )
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding() // This padding() with no arguments does nothing, safe to remove
        ) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel,
                onCameraSuccessfullyBound = {} // If no specific action needed here
            )
            ScannerOverlay(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding) // Apply Scaffold's content padding here for the overlay
            )
        }
    }
}