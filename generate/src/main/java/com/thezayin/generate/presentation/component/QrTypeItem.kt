package com.thezayin.generate.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.thezayin.generate.domain.model.QrType
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun QrTypeItem(
    type: QrType,
    isSelected: Boolean,
    onSelect: (QrType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colorResource(id = R.color.bluishRandom) else MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = modifier
            .clickable { onSelect(type) }
            .fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(5.sdp),
    ) {
        Column(
            modifier = Modifier
                .padding(10.sdp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                tint = MaterialTheme.colorScheme.onSurface,
                painter = when (type) {
                    QrType.CALL -> painterResource(id = R.drawable.ic_call)
                    QrType.SMS -> painterResource(id = R.drawable.ic_sms)
                    QrType.EMAIL -> painterResource(id = R.drawable.ic_email)
                    QrType.WEBSITE -> painterResource(id = R.drawable.ic_website)
                    QrType.TEXT -> painterResource(id = R.drawable.ic_text)
                    QrType.CLIPBOARD -> painterResource(id = R.drawable.ic_clipboard)
                    QrType.WIFI -> painterResource(id = R.drawable.ic_wifi)
                    QrType.CALENDAR -> painterResource(id = R.drawable.ic_calendar)
                    QrType.CONTACT -> painterResource(id = R.drawable.ic_contact)
                    QrType.LOCATION -> painterResource(id = R.drawable.ic_location)
                    QrType.EAN_8 -> painterResource(id = R.drawable.ic_upc)
                    QrType.EAN_13 -> painterResource(id = R.drawable.ic_upc)
                    QrType.UPC_A -> painterResource(id = R.drawable.ic_upc)
                    QrType.UPC_E -> painterResource(id = R.drawable.ic_upc)
                    QrType.CODE_39 -> painterResource(id = R.drawable.ic_barcode_type)
                    QrType.CODE_128 -> painterResource(id = R.drawable.ic_barcode_type)
                    QrType.ITF -> painterResource(id = R.drawable.ic_barcode_type)
                    QrType.PDF_417 -> painterResource(id = R.drawable.ic_barcode_type)
                    QrType.CODABAR -> painterResource(id = R.drawable.ic_barcode_type)
                    QrType.DATAMATRIX -> painterResource(id = R.drawable.ic_datamatrix)
                    QrType.AZTEC -> painterResource(id = R.drawable.ic_itza)
                },
                contentDescription = type.name,
                modifier = Modifier.size(16.sdp)
            )
            Text(
                text = type.name,
                fontSize = 8.ssp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}