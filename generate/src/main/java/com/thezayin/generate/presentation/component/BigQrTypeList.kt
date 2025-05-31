package com.thezayin.generate.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.thezayin.generate.domain.model.QrType
import ir.kaaveh.sdpcompose.sdp

@Composable
fun BigQrTypeList(
    primaryColor: Color,
    allTypes: List<QrType>,
    onTypeSelected: (QrType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.sdp)
    ) {
        items(
            allTypes.chunked(2),
        ) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.sdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowItems.forEach { type ->
                    QrTypeItem(
                        type = type,
                        isSelected = false,
                        onSelect = { onTypeSelected(type) },
                        modifier = Modifier.weight(1f),
                        primaryColor = primaryColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.sdp))
        }
    }
}