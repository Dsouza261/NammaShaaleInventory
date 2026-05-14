package com.nammashaalee.inventory.data.dao

import androidx.room.*
import com.nammashaalee.inventory.data.entity.HealthCheckEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthCheckDao {

    @Query("SELECT * FROM health_checks WHERE assetId = :assetId ORDER BY checkedAt DESC")
    fun getHistoryForAsset(assetId: Long): Flow<List<HealthCheckEntry>>

    @Query("SELECT COUNT(*) FROM health_checks WHERE checkedAt >= :startOfMonth")
    fun getChecksThisMonth(startOfMonth: Long): Flow<Int>

    @Insert
    suspend fun insertHealthCheck(entry: HealthCheckEntry)

    @Query("DELETE FROM health_checks WHERE assetId = :assetId")
    suspend fun deleteHistoryForAsset(assetId: Long)
}