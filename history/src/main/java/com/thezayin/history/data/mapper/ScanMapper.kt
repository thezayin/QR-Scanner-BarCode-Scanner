package com.thezayin.history.data.mapper

import com.thezayin.databases.entity.ScanResultEntity
import com.thezayin.history.domain.model.ScanItem

fun ScanResultEntity.toDomainModel(): ScanItem {
    return ScanItem(
        id = this.id,
        imageUri = this.imageUri,
        scannedText = this.scannedText,
        type = this.type,
        timestamp = this.timestamp,
        isFavorite = this.isFavorite,
        description = this.description
    )
}

fun ScanItem.toEntity(): ScanResultEntity {
    return ScanResultEntity(
        id = this.id,
        imageUri = this.imageUri,
        scannedText = this.scannedText,
        type = this.type,
        timestamp = this.timestamp,
        isFavorite = this.isFavorite,
        description = this.description
    )
}
