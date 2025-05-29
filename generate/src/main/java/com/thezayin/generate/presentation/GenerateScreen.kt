package com.thezayin.generate.presentation

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.thezayin.generate.presentation.component.GenerateScreenContent
import org.koin.compose.koinInject

@Composable
fun GenerateScreen(
    viewModel: GenerateViewModel = koinInject(),
    onNavigateBack: () -> Unit,
    navigateToPremium: () -> Unit
) {
    val activity = LocalActivity.current as Activity

    LaunchedEffect(Unit) {
        viewModel.initManager(activity)
    }

    GenerateScreenContent(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack,
        navigateToPremium = navigateToPremium
    )
}