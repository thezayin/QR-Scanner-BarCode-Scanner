package com.thezayin.framework.session

/**
 * Manager responsible for storing and clearing scan results.
 */
interface ScanSessionManager {
    /**
     * Saves a scan result.
     * @param imageUri The URI or identifier of the image.
     * @param result The scanned QR code result.
     */
    fun saveScanResult(imageUri: String, result: String)

    /**
     * Clears all stored scan results.
     */
    fun clearScanResults()

    /**
     * Returns the list of scan results.
     */
    fun getScanResults(): List<Pair<String, String>>
}