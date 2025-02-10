@file:Suppress("RegExpRedundantEscape", "RegExpSimplifiable")

package com.thezayin.generate.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.thezayin.generate.domain.model.InputFieldData
import com.thezayin.generate.domain.model.QrType
import com.thezayin.generate.presentation.GenerateViewModel
import com.thezayin.generate.presentation.event.GenerateEvent
import com.thezayin.generate.presentation.state.GenerateState
import com.thezayin.values.R

@Composable
fun QrTypeViews(
    state: GenerateState,
    viewModel: GenerateViewModel,
    showLoadingAd: MutableState<Boolean>,
    onEvent: (GenerateEvent) -> Unit
) {
    when (state.selectedType) {
        QrType.CALL -> {
            InputSection(
                viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.phone_number),
                        placeholder = stringResource(id = R.string.enter_phone_number),
                        value = state.callNumber,
                        onValueChange = { onEvent(GenerateEvent.UpdateCallNumber(it)) },
                        validation = { phoneNumber ->
                            phoneNumber.isNotBlank() && phoneNumber.matches(Regex("^\\+?[0-9]*$"))
                        }
                    )
                ),
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.SMS -> {
            InputSection(
                viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.phone_number),
                        placeholder = stringResource(id = R.string.enter_phone_number),
                        value = state.smsNumber,
                        onValueChange = { onEvent(GenerateEvent.UpdateSmsNumber(it)) },
                        validation = { phoneNumber ->
                            phoneNumber.isNotBlank() && phoneNumber.matches(Regex("^\\+?[0-9]*$"))
                        }
                    ),
                    InputFieldData(
                        label = stringResource(id = R.string.message),
                        placeholder = stringResource(id = R.string.enter_message),
                        value = state.smsMessage,
                        onValueChange = { onEvent(GenerateEvent.UpdateSmsMessage(it)) },
                        validation = { message -> message.isNotBlank() }
                    )
                ),
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.EMAIL -> {
            InputSection(
                viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.email_address),
                        placeholder = stringResource(id = R.string.enter_email_address),
                        value = state.emailAddress,
                        onValueChange = { onEvent(GenerateEvent.UpdateEmailAddress(it)) },
                        validation = { email ->
                            email.isNotBlank() &&
                                    email.matches(Regex("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"))
                        }
                    ),
                    InputFieldData(
                        label = stringResource(id = R.string.subject),
                        placeholder = stringResource(id = R.string.enter_subject),
                        value = state.emailSubject,
                        onValueChange = { onEvent(GenerateEvent.UpdateEmailSubject(it)) },
                        validation = { subject -> subject.isNotBlank() }
                    ),
                    InputFieldData(
                        label = stringResource(id = R.string.body),
                        placeholder = stringResource(id = R.string.enter_email_body),
                        value = state.emailBody,
                        onValueChange = { onEvent(GenerateEvent.UpdateEmailBody(it)) },
                        validation = { body -> body.isNotBlank() }
                    )
                ),
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.WEBSITE -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.website_url),
                        placeholder = stringResource(id = R.string.enter_website_url),
                        value = state.websiteUrl,
                        onValueChange = { onEvent(GenerateEvent.UpdateWebsiteUrl(it)) },
                        validation = { url ->
                            url.isNotBlank() && url.matches(Regex("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$"))
                        }
                    )
                ),
                viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.TEXT -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.text),
                        placeholder = stringResource(id = R.string.enter_text),
                        value = state.text,
                        onValueChange = { onEvent(GenerateEvent.UpdateText(it)) },
                        validation = { text -> text.isNotBlank() }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.CLIPBOARD -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.clipboard_text),
                        placeholder = stringResource(id = R.string.enter_clipboard_text),
                        value = state.clip,
                        onValueChange = { onEvent(GenerateEvent.UpdateClipboard(it)) },
                        validation = { clip -> clip.isNotBlank() }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.WIFI -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.ssid),
                        placeholder = stringResource(id = R.string.enter_ssid),
                        value = state.wifiSsid,
                        onValueChange = { onEvent(GenerateEvent.UpdateWifiSsid(it)) },
                        validation = { ssid -> ssid.isNotBlank() }
                    ),
                    InputFieldData(
                        label = stringResource(id = R.string.password),
                        placeholder = stringResource(id = R.string.enter_password),
                        value = state.wifiPassword,
                        onValueChange = { onEvent(GenerateEvent.UpdateWifiPassword(it)) },
                        validation = { password -> password.isNotBlank() }
                    ),
                    InputFieldData(
                        label = stringResource(id = R.string.encryption),
                        placeholder = stringResource(id = R.string.enter_encryption),
                        value = state.wifiEncryption,
                        onValueChange = { onEvent(GenerateEvent.UpdateWifiEncryption(it)) },
                        validation = { encryption -> encryption.isNotBlank() }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.CALENDAR -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.event_title),
                        placeholder = stringResource(id = R.string.enter_event_title),
                        value = state.calendarTitle,
                        onValueChange = { onEvent(GenerateEvent.UpdateCalendarTitle(it)) },
                        validation = { title -> title.isNotBlank() }
                    ),
                    InputFieldData(
                        label = stringResource(id = R.string.description),
                        placeholder = stringResource(id = R.string.enter_description),
                        value = state.calendarDescription,
                        onValueChange = { onEvent(GenerateEvent.UpdateCalendarDescription(it)) },
                        validation = { description -> description.isNotBlank() }
                    ),
                    InputFieldData(
                        label = stringResource(id = R.string.start_timestamp),
                        placeholder = stringResource(id = R.string.enter_start_time),
                        value = state.calendarStart,
                        onValueChange = { onEvent(GenerateEvent.UpdateCalendarStart(it)) },
                        validation = { start -> start.isNotBlank() }
                    ),
                    InputFieldData(
                        label = stringResource(id = R.string.end_timestamp),
                        placeholder = stringResource(id = R.string.enter_end_time),
                        value = state.calendarEnd,
                        onValueChange = { onEvent(GenerateEvent.UpdateCalendarEnd(it)) },
                        validation = { end -> end.isNotBlank() }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.CODABAR -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.codabar_code),
                        placeholder = stringResource(id = R.string.enter_codabar_code),
                        value = state.codabarCode,
                        onValueChange = { onEvent(GenerateEvent.UpdateCodabar(it)) },
                        validation = { code ->
                            code.isNotBlank() && code.matches(Regex("^[A-D0-9\\$\\/+\\-\\.]+$"))
                        }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.ITF -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.itf_code),
                        placeholder = stringResource(id = R.string.enter_itf_code),
                        value = state.itfCode,
                        onValueChange = { onEvent(GenerateEvent.UpdateItf(it)) },
                        validation = { code ->
                            code.isNotBlank() && code.matches(Regex("^[0-9]{6,14}$"))
                        }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.UPC_E -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.upc_e_code),
                        placeholder = stringResource(id = R.string.enter_upc_e_code),
                        value = state.upcECode,
                        onValueChange = { onEvent(GenerateEvent.UpdateUpcE(it)) },
                        validation = { code ->
                            code.isNotBlank() && code.matches(Regex("^[0-9]{6}$"))
                        }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.UPC_A -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.upc_a_code),
                        placeholder = stringResource(id = R.string.enter_upc_a_code),
                        value = state.upcACode,
                        onValueChange = { onEvent(GenerateEvent.UpdateUpcA(it)) },
                        validation = { code ->
                            code.isNotBlank() && code.matches(Regex("^[0-9]{12}$"))
                        }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.EAN_13 -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.ean_13_code),
                        placeholder = stringResource(id = R.string.enter_ean_13_code),
                        value = state.ean13Code,
                        onValueChange = { onEvent(GenerateEvent.UpdateEan13(it)) },
                        validation = { code ->
                            code.isNotBlank() && code.matches(Regex("^[0-9]{13}$"))
                        }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.EAN_8 -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.ean_8_code),
                        placeholder = stringResource(id = R.string.enter_ean_8_code),
                        value = state.ean8Code,
                        onValueChange = { onEvent(GenerateEvent.UpdateEan8(it)) },
                        validation = { code ->
                            code.isNotBlank() && code.matches(Regex("^[0-9]{8}$"))
                        }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.CONTACT -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.contact_name),
                        placeholder = stringResource(id = R.string.enter_contact_name),
                        value = state.contactName,
                        onValueChange = { onEvent(GenerateEvent.UpdateContactName(it)) },
                        validation = { it.isNotBlank() }
                    ),
                    InputFieldData(
                        label = stringResource(id = R.string.contact_phone),
                        placeholder = stringResource(id = R.string.enter_contact_phone),
                        value = state.contactPhone,
                        onValueChange = { onEvent(GenerateEvent.UpdateContactPhone(it)) },
                        validation = { it.isNotBlank() && it.matches(Regex("^[0-9]{10,15}$")) }
                    ),
                    InputFieldData(
                        label = stringResource(id = R.string.contact_email),
                        placeholder = stringResource(id = R.string.enter_contact_email),
                        value = state.contactEmail,
                        onValueChange = { onEvent(GenerateEvent.UpdateContactEmail(it)) },
                        validation = {
                            it.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(
                                it
                            ).matches()
                        }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.LOCATION -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.latitude),
                        placeholder = stringResource(id = R.string.enter_latitude),
                        value = state.locationLat,
                        onValueChange = { onEvent(GenerateEvent.UpdateLocationLat(it)) },
                        validation = { it.isNotBlank() && it.matches(Regex("^[+-]?([0-9]{1,2}|1[0-7][0-9]|180)(\\.[0-9]+)?$")) }
                    ),
                    InputFieldData(
                        label = stringResource(id = R.string.longitude),
                        placeholder = stringResource(id = R.string.enter_longitude),
                        value = state.locationLong,
                        onValueChange = { onEvent(GenerateEvent.UpdateLocationLong(it)) },
                        validation = { it.isNotBlank() && it.matches(Regex("^[+-]?(180|1[0-7][0-9]|[1-9]?[0-9])$")) }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.CODE_39 -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.code_39),
                        placeholder = stringResource(id = R.string.enter_code_39),
                        value = state.code39Code,
                        onValueChange = { onEvent(GenerateEvent.UpdateCode39(it)) },
                        validation = { it.isNotBlank() && it.matches(Regex("^[A-Z0-9\\-\\$\\/\\+\\.\\%\\*]+$")) }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.CODE_128 -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.code_128),
                        placeholder = stringResource(id = R.string.enter_code_128),
                        value = state.code128Code,
                        onValueChange = { onEvent(GenerateEvent.UpdateCode128(it)) },
                        validation = { it.isNotBlank() && it.matches(Regex("^[\\x21-\\x7E]+$")) }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.PDF_417 -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.pdf_417_code),
                        placeholder = stringResource(id = R.string.enter_pdf_417_code),
                        value = state.pdf417Code,
                        onValueChange = { onEvent(GenerateEvent.UpdatePdf417(it)) },
                        validation = { it.isNotBlank() && it.length >= 2 }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.DATAMATRIX -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.datamatrix_code),
                        placeholder = stringResource(id = R.string.enter_datamatrix_code),
                        value = state.dataMatrixCode,
                        onValueChange = { onEvent(GenerateEvent.UpdateDataMatrix(it)) },
                        validation = { it.isNotBlank() && it.matches(Regex("^[A-Za-z0-9]+$")) }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }

        QrType.AZTEC -> {
            InputSection(
                fields = listOf(
                    InputFieldData(
                        label = stringResource(id = R.string.aztec_code),
                        placeholder = stringResource(id = R.string.enter_aztec_code),
                        value = state.aztecCode,
                        onValueChange = { onEvent(GenerateEvent.UpdateAztec(it)) },
                        validation = { it.isNotBlank() && it.matches(Regex("^[A-Za-z0-9]+$")) }
                    )
                ), viewModel = viewModel,
                showLoadingAd = showLoadingAd,
                onAction = { onEvent(GenerateEvent.GenerateQrCode) }
            )
        }
    }
}
