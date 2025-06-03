package com.thezayin.history.data.mapper

import com.thezayin.databases.entity.QrItemEntity
import com.thezayin.history.domain.model.CreateItem

fun QrItemEntity.toDomainModel(): CreateItem {
    return CreateItem(
        id = this.id,
        title = this.qrType,
        content = this.inputData,
        imageUri = this.imageUri,
        timestamp = this.timestamp
    )
}

fun CreateItem.toEntity(): QrItemEntity {
    return QrItemEntity(
        id = this.id,
        qrType = this.title,
        inputData = this.content,
        imageUri = this.imageUri,
        timestamp = this.timestamp
    )
}