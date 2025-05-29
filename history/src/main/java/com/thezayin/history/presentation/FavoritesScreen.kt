package com.thezayin.history.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.thezayin.history.presentation.components.FavoritesScreenContent
import com.thezayin.history.presentation.event.HistoryEvent
import org.koin.compose.koinInject

@Composable
fun FavoritesScreen(
    onNavigateBack: () -> Unit,
    navigateToScanItem: () -> Unit,
    navigateToPremium: () -> Unit
) {
    val viewModel: HistoryViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    val favoriteItems = state.scanItems.filter { it.isFavorite }

    FavoritesScreenContent(
        isPremium = viewModel.pref.isPremiumFlow.value,
        favoriteItems = favoriteItems,
        onNavigateBack = onNavigateBack,
        onItemClicked = { scanItem ->
            viewModel.onEvent(HistoryEvent.OpenScanItem(scanItem))
            navigateToScanItem()
        },
        navigateToPremium = navigateToPremium,
        onDelete = { item ->
            viewModel.onEvent(HistoryEvent.DeleteScanItem(item))
        }
    )
}
