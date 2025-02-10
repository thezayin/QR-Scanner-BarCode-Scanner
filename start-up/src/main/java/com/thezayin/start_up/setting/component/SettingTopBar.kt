package com.thezayin.start_up.setting.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.thezayin.values.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = {
            Text(text = stringResource(id = R.string.settings))
        },
        navigationIcon = {
            IconButton(onClick = {
                onNavigateBack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        }
    )
}
