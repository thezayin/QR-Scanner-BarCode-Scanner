package com.thezayin.framework.session

/**
 * An in-memory implementation of [ScanSessionManager] used to store QR scan results
 * without persisting them to disk or a database. This class is typically useful for
 * temporary sessions where data does not need to survive app restarts.
 */
class InMemoryScanSessionManager : ScanSessionManager {

    /**
     * A mutable list holding pairs of (imageUri, result) for each scan,
     * maintained only in memory. Once this object is released or the app
     * process is killed, the data is lost.
     */
    private val scanResults = mutableListOf<Pair<String, String>>()

    /**
     * Saves a scanned result to the in-memory list.
     *
     * @param imageUri The URI (or path) of the scanned image.
     * @param result The text extracted from the scanned QR code or barcode.
     */
    override fun saveScanResult(imageUri: String, result: String) {
        scanResults.add(imageUri to result)
    }

    /**
     * Clears all currently stored scan results.
     */
    override fun clearScanResults() {
        scanResults.clear()
    }

    /**
     * Retrieves a copy of all scan results stored in memory.
     *
     * @return A list of pairs containing (imageUri, result) for each scan.
     */
    override fun getScanResults(): List<Pair<String, String>> = scanResults.toList()
}