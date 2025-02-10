package com.thezayin.databases.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a generated QR code record.
 *
 * - [qrType]: The type of QR code (for example, "CALL", "EMAIL", "CALENDAR", etc.).
 * - [inputData]: A JSON string representing the user inputs (this may contain a single field or multiple fields depending on the type).
 * - [imageUri]: A String representing the location (URI or file path) of the saved generated QR image.
 * - [timestamp]: The time (in milliseconds) when this record was created.
 */
@Entity(tableName = "qr_items")
data class QrItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val qrType: String,
    val inputData: String,
    val imageUri: String,
    val timestamp: Long = System.currentTimeMillis()
)
