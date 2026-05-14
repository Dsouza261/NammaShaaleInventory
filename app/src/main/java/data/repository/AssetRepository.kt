package com.nammashaalee.inventory.data.repository

import com.nammashaalee.inventory.data.dao.AssetDao
import com.nammashaalee.inventory.data.dao.HealthCheckDao
import com.nammashaalee.inventory.data.entity.Asset
import com.nammashaalee.inventory.data.entity.AssetCondition
import com.nammashaalee.inventory.data.entity.HealthCheckEntry
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetRepository @Inject constructor(
    private val assetDao: AssetDao,
    private val healthCheckDao: HealthCheckDao
) {
    val allAssets: Flow<List<Asset>> = assetDao.getAllAssets()
    val totalCount: Flow<Int> = assetDao.getTotalCount()
    val workingCount: Flow<Int> = assetDao.getCountByCondition(AssetCondition.WORKING)
    val repairCount: Flow<Int> = assetDao.getCountByCondition(AssetCondition.NEEDS_REPAIR)
    val brokenCount: Flow<Int> = assetDao.getCountByCondition(AssetCondition.BROKEN)

    fun getAssetsByCondition(condition: AssetCondition) =
        assetDao.getAssetsByCondition(condition)

    fun getAssetById(id: Long): Flow<Asset?> = assetDao.getAssetById(id)

    fun searchAssets(query: String): Flow<List<Asset>> = assetDao.searchAssets(query)

    fun getHealthHistory(assetId: Long): Flow<List<HealthCheckEntry>> =
        healthCheckDao.getHistoryForAsset(assetId)

    fun getChecksThisMonth(): Flow<Int> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        return healthCheckDao.getChecksThisMonth(cal.timeInMillis)
    }

    suspend fun insertAsset(asset: Asset): Long = assetDao.insertAsset(asset)

    suspend fun updateAsset(asset: Asset) {
        val updated = asset.copy(updatedAt = System.currentTimeMillis())
        assetDao.updateAsset(updated)
        // Also log a health check entry for history
        healthCheckDao.insertHealthCheck(
            HealthCheckEntry(
                assetId = asset.id,
                condition = asset.condition,
                note = asset.issueNote,
                photoPath = asset.photoPath
            )
        )
    }

    suspend fun deleteAsset(asset: Asset) = assetDao.deleteAsset(asset)
}