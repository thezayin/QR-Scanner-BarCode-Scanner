package com.thezayin.generate.presentation

import androidx.compose.runtime.Composable
import com.thezayin.generate.presentation.component.GenerateScreenContent
import org.koin.compose.koinInject

@Composable
fun GenerateScreen(
    viewModel: GenerateViewModel = koinInject(),
    onNavigateBack: () -> Unit
) {
    GenerateScreenContent(
        viewModel = viewModel,
        onNavigateBack = onNavigateBack
    )
}