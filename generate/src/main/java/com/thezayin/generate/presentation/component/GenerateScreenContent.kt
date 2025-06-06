package com.thezayin.generate.presentation.component

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.thezayin.generate.domain.model.QrType
import com.thezayin.generate.presentation.GenerateViewModel
import com.thezayin.generate.presentation.event.GenerateEvent
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateScreenContent(
    viewModel: GenerateViewModel,
    onNavigateBack: () -> Unit = {},
    navigateToPremium: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val adManager = viewModel.adManager
    val allTypes = QrType.entries

    val activity = LocalActivity.current as Activity
    val enterTransition: EnterTransition = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing))

    val exitTransition: ExitTransition = ExitTransition.None

    if (state.showDownloadSuccess) {
        DownloadSuccessBottomSheet(
            onDismiss = { viewModel.onEvent(GenerateEvent.DismissDownloadSuccess) }
        )
    }

    Scaffold(
        modifier = Modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                Spacer(modifier = Modifier.size(10.sdp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (!state.showMainListOnly) {
                            viewModel.onEvent(GenerateEvent.BackPressed)
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onSurface,
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }

                    Text(
                        fontSize = 16.ssp,
                        text = stringResource(id = R.string.qr_generator),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (!viewModel.pref.isPremiumFlow.value) {
                        IconButton(onClick = {
                            navigateToPremium()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_crown),
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(15.sdp),
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(30.sdp))
                    }
                }
            }
        }
    ) { paddingValues ->
        AnimatedVisibility(
            visible = state.showMainListOnly,
            enter = enterTransition,
            exit = exitTransition
        ) {
            BigQrTypeList(
                primaryColor = viewModel.pref.getPrimaryColor(),
                allTypes = allTypes,
                onTypeSelected = { type ->
                    adManager.showAd(
                        activity = activity,
                        showAd = viewModel.remoteConfig.adConfigs.adOnCreateOption,
                        onNext = {
                            viewModel.onEvent(GenerateEvent.SelectType(type))
                        },
                    )
                },
                modifier = Modifier.padding(paddingValues)
            )
        }

        AnimatedVisibility(
            visible = !state.showMainListOnly && state.generatedQrBitmap != null,
            enter = enterTransition,
            exit = exitTransition
        ) {
            GeneratedQrContent(
                viewModel = viewModel,
                remoteConfig = viewModel.remoteConfig,
                state = state,
                onEvent = { viewModel.onEvent(it) },
                modifier = Modifier.padding(paddingValues)
            )
        }

        AnimatedVisibility(
            visible = !state.showMainListOnly && state.generatedQrBitmap == null,
            enter = enterTransition,
            exit = exitTransition
        ) {
            DetailedViewContent(
                state = state,
                allTypes = allTypes,
                onEvent = { viewModel.onEvent(it) },
                modifier = Modifier.padding(paddingValues),
                viewModel = viewModel,
            )
        }
    }
}
