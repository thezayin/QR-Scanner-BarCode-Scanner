package com.thezayin.history.data.repository

import com.thezayin.databases.dao.QrItemDao
import com.thezayin.databases.dao.ScanResultDao
import com.thezayin.history.data.mapper.toDomainModel
import com.thezayin.history.data.mapper.toEntity
import com.thezayin.history.domain.model.CreateItem
import com.thezayin.history.domain.model.ScanItem
import com.thezayin.history.domain.repository.HistoryRepository

class HistoryRepositoryImpl(
    private val scanDao: ScanResultDao,
    private val createDao: QrItemDao
) : HistoryRepository {

    override suspend fun getAllCreateItems(): List<CreateItem> {
        val entities = createDao.getAll()
        return entities.map { it.toDomainModel() }
    }

    override suspend fun deleteCreateItem(item: CreateItem) {
        val entity = item.toEntity().copy(imageUri = "")
        createDao.delete(entity)
    }

    override suspend fun getAllScanItems(): List<ScanItem> {
        val entities = scanDao.getAll()
        val domainModels = entities.map { it.toDomainModel() }
        return domainModels
    }

    override suspend fun updateScanFavorite(item: ScanItem) {
        val entity = item.toEntity()
        scanDao.update(entity)
    }

    override suspend fun deleteScanItem(item: ScanItem) {
        val entity = item.toEntity()
        scanDao.delete(entity)
    }
}