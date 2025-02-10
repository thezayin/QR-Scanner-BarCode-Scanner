package com.thezayin.databases.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.thezayin.databases.entity.ScanResultEntity

@Dao
interface ScanResultDao {

    @Insert
    suspend fun insert(scanResult: ScanResultEntity): Long

    @Update
    suspend fun update(scanResult: ScanResultEntity)

    @Delete
    suspend fun delete(scanResult: ScanResultEntity)

    @Query("SELECT * FROM scan_results")
    suspend fun getAll(): List<ScanResultEntity>
}
