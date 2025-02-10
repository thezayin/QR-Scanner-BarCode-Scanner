package com.thezayin.databases.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.thezayin.databases.entity.QrItemEntity

@Dao
interface QrItemDao {
    @Insert
    suspend fun insert(qrItem: QrItemEntity): Long

    @Update
    suspend fun update(qrItem: QrItemEntity)

    @Delete
    suspend fun delete(qrItem: QrItemEntity)

    @Query("SELECT * FROM qr_items ORDER BY timestamp DESC")
    suspend fun getAll(): List<QrItemEntity>
}
