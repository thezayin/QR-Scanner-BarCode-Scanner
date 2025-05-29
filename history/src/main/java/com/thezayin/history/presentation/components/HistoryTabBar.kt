package com.thezayin.history.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.thezayin.history.presentation.state.HistoryTab
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTabBar(
    isPremium: Boolean,
    selectedTab: HistoryTab,
    onTabSelected: (HistoryTab) -> Unit,
    onNavigateUp: () -> Unit,
    navigateToPremium: () -> Unit
) {
    val tabs = listOf(HistoryTab.SCAN, HistoryTab.CREATED)
    Column {
        Spacer(modifier = Modifier.size(10.sdp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                onNavigateUp()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
            Text(
                text = stringResource(id = R.string.history),
                fontSize = 16.ssp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (!isPremium) {
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
            }else{
                Spacer(modifier = Modifier.size(30.sdp))
            }
        }
        TabRow(
            modifier = Modifier.padding(horizontal = 50.sdp),
            containerColor = MaterialTheme.colorScheme.background,
            selectedTabIndex = tabs.indexOf(selectedTab)
        ) {
            tabs.forEachIndexed { _, tab ->
                Tab(
                    modifier = Modifier.padding(horizontal = 8.sdp),
                    selected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = tab.name,
                            fontSize = 8.ssp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    })
            }
        }
    }
}