package com.thezayin.history.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thezayin.history.domain.model.ScanItem
import ir.kaaveh.sdpcompose.sdp

@Composable
fun ScanList(
    modifier: Modifier,
    items: List<ScanItem>,
    onItemClick: (ScanItem) -> Unit,
    onToggleFavorite: (ScanItem) -> Unit,
    onDelete: (ScanItem) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.sdp)
    ) {
        items(items) { item ->
            ScanItemCard(
                item = item,
                onItemClick = onItemClick,
                onToggleFavorite = onToggleFavorite,
                onDelete = onDelete
            )
        }
    }
}