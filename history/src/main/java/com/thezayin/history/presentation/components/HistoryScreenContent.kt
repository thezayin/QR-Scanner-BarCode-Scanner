package com.thezayin.history.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.thezayin.history.domain.model.CreateItem
import com.thezayin.history.domain.model.ScanItem
import com.thezayin.history.presentation.state.HistoryState
import com.thezayin.history.presentation.state.HistoryTab
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.ssp

@Composable
fun HistoryScreenContent(
    isPremium: Boolean,
    state: HistoryState,
    onNavigateUp: () -> Unit,
    onTabSelected: (HistoryTab) -> Unit,
    onScanItemClick: (ScanItem) -> Unit,
    onToggleFavorite: (ScanItem) -> Unit,
    onScanDelete: (ScanItem) -> Unit,
    onCreateDelete: (CreateItem) -> Unit,
    onItemClicked: (CreateItem) -> Unit,
    navigateToPremium: () -> Unit
) {
    val enterAnimation = slideInHorizontally(
        initialOffsetX = { it }, animationSpec = tween(durationMillis = 600)
    ) + fadeIn(animationSpec = tween(durationMillis = 600))
    val exitAnimation = slideOutHorizontally(
        targetOffsetX = { -it }, animationSpec = tween(durationMillis = 600)
    ) + fadeOut(animationSpec = tween(durationMillis = 600))

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, topBar = {
            HistoryTabBar(
                isPremium = isPremium,
                onNavigateUp = onNavigateUp,
                selectedTab = state.selectedTab,
                onTabSelected = onTabSelected,
                navigateToPremium = navigateToPremium
            )
        }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(id = R.string.loading))
                }
            } else {
                AnimatedVisibility(
                    visible = state.selectedTab == HistoryTab.SCAN,
                    enter = enterAnimation,
                    exit = exitAnimation
                ) {
                    if (state.scanItems.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_record_found),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.ssp,
                            )
                        }
                    } else {
                        ScanList(
                            modifier = Modifier.fillMaxSize(),
                            items = state.scanItems,
                            onItemClick = { onScanItemClick(it) },
                            onToggleFavorite = { onToggleFavorite(it) },
                            onDelete = { onScanDelete(it) })
                    }
                }
                AnimatedVisibility(
                    visible = state.selectedTab == HistoryTab.CREATED,
                    enter = enterAnimation,
                    exit = exitAnimation
                ) {
                    if (state.createItems.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_record_found),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.ssp,
                            )
                        }
                    } else {
                        CreateList(
                            items = state.createItems,
                            onDelete = { onCreateDelete(it) },
                            onItemClick = onItemClicked
                        )
                    }
                }
            }
        }
    }
}