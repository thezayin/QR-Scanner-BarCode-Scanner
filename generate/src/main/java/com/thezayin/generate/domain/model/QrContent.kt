package com.thezayin.generate.domain.model

/**
 * A sealed class that represents the different types of data that can be encoded
 * into a QR code or barcode. Each subclass corresponds to a specific format and
 * holds the necessary information for that format.
 */
sealed class QrContent {

    /**
     * Represents an ITF (Interleaved 2 of 5) barcode.
     *
     * @param code The numeric code to be encoded in the ITF barcode.
     */
    data class Itf(val code: String) : QrContent()

    /**
     * Represents plain text to be encoded as a QR code.
     *
     * @param text The text to encode.
     */
    data class Text(val text: String) : QrContent()

    /**
     * Represents an EAN-8 barcode.
     *
     * @param code The 8-digit EAN code.
     */
    data class Ean8(val code: String) : QrContent()

    /**
     * Represents a UPC-A barcode.
     *
     * @param code The UPC-A code (typically 12 digits).
     */
    data class UpcA(val code: String) : QrContent()

    /**
     * Represents a UPC-E barcode.
     *
     * @param code The UPC-E code, which is a compressed version of UPC-A.
     */
    data class UpcE(val code: String) : QrContent()

    /**
     * Represents an EAN-13 barcode.
     *
     * @param code The 13-digit EAN code.
     */
    data class Ean13(val code: String) : QrContent()

    /**
     * Represents an Aztec barcode.
     *
     * @param code The content to encode into an Aztec barcode.
     */
    data class Aztec(val code: String) : QrContent()

    /**
     * Represents a PDF417 barcode.
     *
     * @param code The data to encode using PDF417.
     */
    data class Pdf417(val code: String) : QrContent()

    /**
     * Represents a website URL.
     *
     * @param url The website URL to be encoded.
     */
    data class Website(val url: String) : QrContent()

    /**
     * Represents a Code 39 barcode.
     *
     * @param code The alphanumeric code to be encoded using Code 39.
     */
    data class Code39(val code: String) : QrContent()

    /**
     * Represents a Code 128 barcode.
     *
     * @param code The code (alphanumeric or numeric) to encode using Code 128.
     */
    data class Code128(val code: String) : QrContent()

    /**
     * Represents a Codabar barcode.
     *
     * @param code The code to encode using Codabar.
     */
    data class Codabar(val code: String) : QrContent()

    /**
     * Represents clipboard text to be encoded.
     *
     * @param clip The text to be encoded as clipboard content.
     */
    data class Clipboard(val clip: String) : QrContent()

    /**
     * Represents a DataMatrix barcode.
     *
     * @param code The data to encode in a DataMatrix barcode.
     */
    data class DataMatrix(val code: String) : QrContent()

    /**
     * Represents a phone call.
     *
     * @param phoneNumber The phone number to dial when scanned.
     */
    data class Call(val phoneNumber: String) : QrContent()

    /**
     * Represents an SMS message.
     *
     * @param phoneNumber The phone number to which the SMS will be sent.
     * @param message The SMS message content.
     */
    data class Sms(val phoneNumber: String, val message: String) : QrContent()

    /**
     * Represents a geographic location.
     *
     * @param latitude The latitude coordinate.
     * @param longitude The longitude coordinate.
     */
    data class Location(val latitude: String, val longitude: String) : QrContent()

    /**
     * Represents a WiFi network configuration.
     *
     * @param ssid The WiFi network name.
     * @param password The WiFi password.
     * @param encryption The encryption type (e.g., WPA, WEP).
     */
    data class Wifi(val ssid: String, val password: String, val encryption: String) : QrContent()

    /**
     * Represents a contact (vCard).
     *
     * @param name The full name of the contact.
     * @param phoneNumber The contact's phone number.
     * @param email The contact's email address.
     */
    data class Contact(val name: String, val phoneNumber: String, val email: String) : QrContent()

    /**
     * Represents an email message.
     *
     * @param emailAddress The recipient's email address.
     * @param subject The email subject.
     * @param body The body of the email.
     */
    data class Email(val emailAddress: String, val subject: String, val body: String) : QrContent()

    /**
     * Represents a calendar event in a simplified ICS format.
     *
     * @param title The title of the event.
     * @param description A description of the event.
     * @param startTime The event start time as a Unix timestamp (milliseconds).
     * @param endTime The event end time as a Unix timestamp (milliseconds).
     */
    data class Calendar(
        val title: String,
        val description: String,
        val startTime: Long,
        val endTime: Long
    ) : QrContent()
}