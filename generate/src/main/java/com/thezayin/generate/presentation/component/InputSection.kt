package com.thezayin.generate.presentation.component

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.thezayin.framework.ads.functions.rewardedAd
import com.thezayin.generate.domain.model.InputFieldData
import com.thezayin.generate.presentation.GenerateViewModel
import com.thezayin.values.R
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
fun InputSection(
    fields: List<InputFieldData>,
    buttonText: String? = null,
    viewModel: GenerateViewModel,
    showLoadingAd: MutableState<Boolean>,
    onAction: () -> Unit
) {
    val activity = LocalContext.current as Activity
    val allFieldsValid = fields.all { field ->
        field.validation(field.value)
    }
    val effectiveButtonText = buttonText ?: stringResource(id = R.string.generate_qr_code)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.sdp)
    ) {
        fields.forEach { field ->
            CustomTextField(
                value = field.value,
                onValueChange = field.onValueChange,
                label = field.label,
                placeholder = field.placeholder,
                validation = field.validation,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.sdp))
        }
        Spacer(modifier = Modifier.height(40.sdp))
        Button(
            onClick = {
                activity.rewardedAd(
                    showAd = viewModel.remoteConfig.adConfigs.adOnGenerateQr,
                    adUnitId = viewModel.remoteConfig.adUnits.rewardedAd,
                    showLoading = { showLoadingAd.value = true },
                    hideLoading = { showLoadingAd.value = false },
                    callback = {
                        onAction()
                    },
                )

            },
            enabled = allFieldsValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(35.sdp),
            shape = RoundedCornerShape(8.sdp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.primary),
                disabledContainerColor = colorResource(id = R.color.greyish),
            ),
        ) {
            Text(
                text = effectiveButtonText,
                fontSize = 12.ssp,
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                color = colorResource(id = R.color.white)
            )
        }
    }
}