package com.thezayin.history.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thezayin.history.domain.model.CreateItem
import ir.kaaveh.sdpcompose.sdp

@Composable
fun CreateList(
    items: List<CreateItem>,
    onDelete: (CreateItem) -> Unit,
    onItemClick: (CreateItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.sdp)
    ) {
        items(items) { item ->
            CreateItemCard(item = item, onDelete = onDelete, onItemClicked = onItemClick)
        }
    }
}
