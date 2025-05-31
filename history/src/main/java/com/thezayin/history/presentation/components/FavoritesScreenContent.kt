package com.thezayin.history.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.thezayin.history.domain.model.ScanItem
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreenContent(
    isPremium: Boolean,
    favoriteItems: List<ScanItem>,
    onNavigateBack: () -> Unit,
    onItemClicked: (ScanItem) -> Unit,
    onDelete: (ScanItem) -> Unit,
    navigateToPremium: () -> Unit

) {
    Scaffold(
        topBar = {
            Column {
                Spacer(modifier = Modifier.size(10.sdp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        onNavigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.favorites),
                        fontSize = 16.ssp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (!isPremium) IconButton(onClick = {
                        navigateToPremium()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_crown),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(15.sdp),
                            contentDescription = stringResource(id = R.string.back)
                        )
                    } else {
                        Spacer(modifier = Modifier.size(30.sdp))
                    }
                }
            }
        }
    ) { paddingValues ->
        if (favoriteItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_favorite_items),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(favoriteItems) { item ->
                    FavoriteScanItemCard(
                        item = item,
                        onDelete = onDelete,
                        onItemClicked = onItemClicked
                    )
                }
            }
        }
    }
}