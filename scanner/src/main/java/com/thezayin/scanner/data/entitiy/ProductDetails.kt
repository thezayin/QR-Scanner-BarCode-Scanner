package com.thezayin.scanner.data.entitiy

import com.thezayin.databases.entity.ScanResultEntity
import com.thezayin.scanner.domain.model.ResultScreenItem

/**
 * Converts a [ResultScreenItem] (domain model) into a [ScanResultEntity] (Room entity),
 * preparing it for insertion or update in the local database.
 *
 * Note: This function sets the `timestamp` to the current system time using
 * [System.currentTimeMillis]. If your domain model already contains a valid timestamp,
 * you may prefer to use that instead to preserve its original value.
 *
 * @receiver The domain model representing the scanned result.
 * @return A new [ScanResultEntity] with fields mapped from the [ResultScreenItem].
 */
fun ResultScreenItem.toEntity(): ScanResultEntity {
    return ScanResultEntity(
        id = this.id,
        imageUri = this.imageUri,
        scannedText = this.result,
        timestamp = System.currentTimeMillis(),
        isFavorite = this.isFavorite,
        type = this.type,
        description = this.name
    )
}