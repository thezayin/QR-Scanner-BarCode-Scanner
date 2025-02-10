package com.thezayin.databases.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thezayin.databases.dao.QrItemDao
import com.thezayin.databases.dao.ScanResultDao
import com.thezayin.databases.entity.QrItemEntity
import com.thezayin.databases.entity.ScanResultEntity

@Database(
    entities = [ScanResultEntity::class, QrItemEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanResultDao(): ScanResultDao
    abstract fun createItemDao(): QrItemDao
}