package com.thezayin.generate.presentation.component

import android.app.Activity
import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
    onAction: () -> Unit
) {
    val adManager = viewModel.adManager
    val activity = LocalActivity.current as Activity
    val state by viewModel.state.collectAsState()

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
                // The check for isGenerating is now in the ViewModel's onEvent
                adManager.showAd(
                    activity = activity,
                    showAd = viewModel.remoteConfig.adConfigs.adOnGenerateQr,
                    onNext = {
                        onAction()
                    },
                )
            },
            enabled = allFieldsValid && !state.isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(35.sdp),
            shape = RoundedCornerShape(8.sdp),
            colors = ButtonDefaults.buttonColors(
                containerColor = viewModel.pref.getPrimaryColor(),
                disabledContainerColor = colorResource(id = R.color.greyish),
            ),
        ) {
            Text(
                text = if (state.isGenerating) "generating..." else effectiveButtonText,
                fontSize = 12.ssp,
                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                color = colorResource(id = R.color.white)
            )
        }
    }
}