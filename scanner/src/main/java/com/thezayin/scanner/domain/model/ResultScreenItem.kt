package com.thezayin.scanner.domain.model

/**
 * Represents a scanned result, typically displayed on the Result Screen of the application.
 *
 * @property id A unique identifier for the item, matching the primary key in the local database.
 * @property imageUri A string representing the URI or path of the scanned image.
 * @property result The main text or data extracted from the scan (e.g., a barcode number, WiFi config, etc.).
 * @property type The type of content detected, such as "Text", "Barcode", "WIFI", "URL", etc.
 * @property name An optional human-readable name (e.g., product name) if the scanned result corresponds to a product.
 * @property brands An optional string representing the brand(s) if available (e.g., from a product API).
 * @property imageUrl An optional link to an online image (e.g., product image) retrieved from a remote API.
 * @property links An optional map of related links or resources (e.g., for Amazon or eBay product pages).
 * @property timestamp The time (in milliseconds since the Unix epoch) when this item was created or scanned.
 * @property productFound A boolean flag (or null if not applicable) indicating whether product details were successfully fetched.
 * @property isFavorite Indicates whether this item is marked as a favorite by the user.
 */
data class ResultScreenItem(
    val id: Long = 0,
    val imageUri: String,
    val result: String,
    val type: String,
    val name: String? = null,
    val brands: String? = null,
    val imageUrl: String? = null,
    val links: Map<String, String?>? = null,
    val timestamp: Long? = null,
    val productFound: Boolean? = null,
    val isFavorite: Boolean = false
)