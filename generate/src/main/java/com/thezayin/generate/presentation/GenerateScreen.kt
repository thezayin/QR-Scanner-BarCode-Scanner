package com.thezayin.generate.presentation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.thezayin.generate.presentation.component.GenerateScreenContent
import org.koin.compose.koinInject

@Composable
fun GenerateScreen(
    viewModel: GenerateViewModel = koinInject(),
    onNavigateBack: () -> Unit
) {
    val activity = LocalContext.current as Activity

    LaunchedEffect(Unit) {
        viewModel.initManager(activity)
    }

    GenerateScreenContent(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack
    )
}