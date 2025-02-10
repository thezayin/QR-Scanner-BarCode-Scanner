package com.thezayin.databases.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_results")
data class ScanResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageUri: String,
    val scannedText: String,
    val type: String,
    val timestamp: Long,
    @ColumnInfo(name = "isFavorite")
    val isFavorite: Boolean = false,
    @ColumnInfo(name = "description")
    val description: String? = null
)